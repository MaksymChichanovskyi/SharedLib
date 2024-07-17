package ExampleA

class ExampleA {
    static def installMaven(String imageName) {
        docker.image(imageName).pull()
        docker.image(imageName).inside {
            sh "mvn clean package"
        }
 }

return this 
