package ExampleA

def installMaven (String imageName){
def imageName = "maven:3.9.8-amazoncorretto-11"
        docker.image(imageName).pull()
        docker.image(imageName).inside() {
            sh "mvn clean package"
    }
 }

return this 