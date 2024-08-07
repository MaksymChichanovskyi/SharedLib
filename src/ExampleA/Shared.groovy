package ExampleA
import groovy.xml.XmlUtil

def defaultCheckout() 
{
    checkout(scm)
}

def readPomXml()
{
    def pomFileContent = readFile 'pom.xml'
    return new XmlParser().parseText(pomFileContent)
}

def savePomXml(def pomXml){
    def updatedPomFileContent = groovy.xml.XmlUtil.serialize(pomXml)
    writeFile file: 'pom.xml', text: updatedPomFileContent   
}

def updatePomVersion(String buildNumber, def pomXml)
{
    pomXml.version[0].value = "1.0.${buildNumber}-SNAPSHOT"
    echo "Updated pom.xml with build number: ${buildNumber}"
}

def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11")
{
    docker.image(imageName).pull()
    docker.image(imageName).inside() {
        sh "mvn clean package"
    }
}

def getJarPathFromPom(def pomXml)
{
    def artifactId = pomXml.artifactId[0].text()
    def version = pomXml.version[0].text()
    def jarPath = "target/${artifactId}-${version}.jar"
    return jarPath
}

 def getJarSize(String jarPath)
{
    def jarFile = sh(script: "ls -l ${jarPath} | awk '{print \$5}' ", returnStdout: true).trim()
    return jarFile.toInteger()
}
        
def commitPomXmlChanges(String originBranch = "main") {
    sh """
        git config --global user.email "cmaksmim@gmail.com"
        git config --global user.name "Jenkins"
        git add pom.xml 
        git commit  -m  " Update version on pom.xml  " 
         git push --set-upstream origin ${originBranch}
    """
    echo "Committed changes to pom.xml with message: "
}

def mavenApp()
{
    def agentName = 'linux && docker'

    node(agentName) {
        stage('Checkout') {
            defaultCheckout()
        }
        def pomXml = readPomXml()
        stage('Update Pom.xml'){
            updatePomVersion(env.BUILD_NUMBER, pomXml)
            savePomXml(pomXml)
        }
        stage('Build'){
            startBuild()
        }
        stage('Get Size'){
            def jarPath = getJarPathFromPom(pomXml)
            echo "jarPath ${jarPath}"
            def jarSize = getJarSize(jarPath)
            echo "jarSize: ${jarSize}"
            }
        stage('Upload to S3'){    
        sh "aws s3 cp ${env.WORKSPACE}/target/*jar s3://devops-engage-test/education/UploadJar/ "
        }
        /*stage ('Commit Update'){
             //commitPomXmlChanges()
        }*/
        }
    }

return this
