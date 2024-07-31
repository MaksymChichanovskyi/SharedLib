epackage ExampleA
import groovy.xml.XmlUtil


    def defaultCheckout() {
        checkout(scm)
    }


def updatePomVersion(String buildNumber) {
    def pomFilePath = 'pom.xml'
    def pomFileContent = readFile pomFilePath
    def pomXml = new XmlParser().parseText(pomFileContent)
    pomXml.version[0].value = "1.0.${buildNumber}-SNAPSHOT"
    def updatedPomFileContent = groovy.xml.XmlUtil.serialize(pomXml)
    writeFile file: pomFilePath, text: updatedPomFileContent
    echo "Updated pom.xml with build number: ${buildNumber}"
}


    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }
def getJarSizeFromPom() {
    def pomFilePath = 'pom.xml'
    def pomFileContent = readFile pomFilePath
    def pomXml = new XmlParser().parseText(pomFileContent)
    def groupId = pomXml.groupId.text()
    def artifactId = pomXml.artifactId.text()
    def version = pomXml.version.text()
    def jarFileName = "${artifactId}-${version}.jar"
    def jarFilePath = "target/${jarFileName}"
    if (fileExists(jarFilePath)) {
        def jarSize = sh(script: "stat -c%s ${jarFilePath}", returnStdout: true).trim()
        return jarSize
    } else {
        error "JAR file ${jarFilePath} not found"
    }
}

    def mavenApp(){
        def agentName = 'linux && docker'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
    stage ('Update Pom.xml'){
        updatePomVersion(env.BUILD_NUMBER)
    }

  stage('Build'){
      startBuild()
    }
 stage ('Get Size'){
   getJarSize()
     echo "The size of the JAR file is: ${jarSize} bytes"
          }
       }
    }
return this
