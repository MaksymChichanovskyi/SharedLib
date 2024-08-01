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
        script {
                    // Assuming your JAR file is named "myapp.jar" and is located in the target directory
                    def jarSize = new File('target/').size()
                    println "JAR file size: ${jarSize} bytes"
                    // Optionally, you can convert bytes to kilobytes or megabytes
                    def jarSizeKB = jarSize / 1024
                    println "JAR file size: ${jarSizeKB} KB"
                }
        }
      }
    }
return this
