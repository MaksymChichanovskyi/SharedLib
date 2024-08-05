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

def commitPomXmlChanges(String commitMessage) {
    withCredentials([string(credentialsId: 'cf84bbaf-792c-4bac-98ae-b80958b2656f', variable: 'GITHUB_PAT')]) {
    sh '''
        git config --global user.email "cmaksmim@gmail.com"
        git config --global user.name "MaksymChichanovskyi"
        git show-ref
        git init
        git add pom.xml
        git commit -m "Update version on pom.xml${commitMessage}"
        git push origin  refs/remotes/origin/master
    '''
    echo "Committed changes to pom.xml with message: ${commitMessage}"
}
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
        stage ('Commit Update'){
             commitPomXmlChanges("Update pom.xml with build number: ${env.BUILD_NUMBER}")
        }
        }
    }

return this
