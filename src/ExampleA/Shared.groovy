package ExampleA
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

def getArtifactIdAndVersion() {
        def pomFilePath = 'pom.xml'
        def pomFileContent = readFile pomFilePath
        def pomXml = new XmlParser().parseText(pomFileContent)
        def artifactId = pomXml.project.artifactId.text()
        def version = pomXml.project.version.text()
        def jarFileName = "${artifactId}-${version}.jar"
        def jarFile = new File("target/${jarFileName}")

        if (jarFile.exists()) {
            def jarSizeBytes = jarFile.size()
            def jarSizeKB = jarSizeBytes / 1024
            return [artifactId, version, jarFileName, jarSizeBytes, jarSizeKB]
        } else {
            return [artifactId, version, jarFileName, 0, 0]
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
      def shared = new Shared()
      def (artifactId, version, jarFileName, jarSizeBytes, jarSizeKB) = shared.getArtifactIdAndVersion()
      echo "ArtifactId: ${artifactId}, Version: ${version}, JAR file name: ${jarFileName}, JAR file size: ${jarSizeBytes} bytes, ${jarSizeKB} KB"
        }
      }
    }
return this
