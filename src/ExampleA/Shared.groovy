package ExampleA

// Визначення класу Shared
class test {
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

return this
