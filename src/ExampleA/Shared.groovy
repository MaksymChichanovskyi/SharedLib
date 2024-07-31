package ExampleA

    def defaultCheckout() {
        checkout(scm)
    }
def updatePomVersion(String buildNumber) {
            def pomFile = readFile 'pom.xml'
            def updatedPomFile = pomFile.replaceAll('<version>1.0-SNAPSHOT</version>', "<version>1.0.${env.BUILD_NUMBER}</version>")
            writeFile file: 'pom.xml', text: updatedPomFile
            echo "Updated pom.xml with build number: ${env.BUILD_NUMBER}"
}
    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }

    def mavenApp(){
        def agentName = 'linux && docker'
        def someText = 'Hello!'
         

node(agentName) { 
    stage('Checkout') {
        defaultCheckout()
    }
    stage ('Update Pom.xml'){
        updatePomVersion()
    }

  stage('Build'){
      startBuild()
    }
    }
    }

return this
