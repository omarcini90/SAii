pipeline{
    agent any
    options {
        skipStagesAfterUnstable()
    }
    stages{
        stage ("<< CLEAN >>"){
            steps{
                bat "mvn clean"
            }
        }
		stage ("<< TEST >>"){
		    steps{
		        bat "mvn test"		        
		    }
		}
		stage ("<< BUILD >>"){
		    steps{
		        bat "mvn package"
		    }
		}
    }
    post{
        always{
            echo 'Build is finished!'
        }
		success{
		    echo 'Build has also been succeded!'
		}
		failure {
		    echo 'Something went wrong with the Build!'
		    mail to: 'Omar.Rendon@ula.edu.mx',
		    	subject: "Pipeline failed ${currentBuild.fullDisplayName}",
		    	body: "Something went wrong with ${env.BUILD_URL}"
		}
        unstable {
            echo 'The Build is unstable!'
        }
        changed {
            echo 'The Build has changed since the last execution!'
        }
    }

}