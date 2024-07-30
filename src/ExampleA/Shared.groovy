package ExampleA

// Визначення класу Shared
class Shared {
    // Метод для Checkout
    def defaultCheckout() {
        checkout(scm)
    }

    // Метод для старту будівництва
    def startBuild(String imageName = "maven:3.9.8-amazoncorretto-11") {
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
        }
    }
}

// Метод для виконання всіх стадій
def mavenApp() {
    def agentName = 'linux && docker'
    def someText = 'Hello!'

    node(agentName) {
        stage('Checkout') {
            def shared = new Shared()
            shared.defaultCheckout()
        }

        stage('Build') {
            def shared = new Shared()
            shared.startBuild()
        }
    }
}

// Виклик основного методу
mavenApp()
