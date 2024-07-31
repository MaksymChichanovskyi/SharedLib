epackage ExampleA
import groovy.xml.XmlUtil


    def defaultCheckout() {
        checkout(scm)
    }



def updatePomVersion(String buildNumber) {
    // Читання вмісту файлу pom.xml
    def pomFileContent = readFile('pom.xml')
    
    // Використання регулярного виразу для знаходження та заміни версії
    def updatedPomFileContent = pomFileContent.replaceAll(/<version>\d+\.\d+-SNAPSHOT<\/version>/, "<version>1.0.${env.buildNumber}</version>")
    
    // Запис зміненого XML назад у файл
    writeFile(file: 'pom.xml', text: updatedPomFileContent)
    
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
