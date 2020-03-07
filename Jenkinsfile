node {
    stage("checkout") {
        checkout([
                $class                           : 'GitSCM',
                branches                         : [[name: '**']],
                doGenerateSubmoduleConfigurations: false,
                extensions                       : [],
                submoduleCfg                     : [],
                userRemoteConfigs                : [
                        [credentialsId: 'se3-gitlab',
                         url          : 'http://212.129.149.40/171250558_teamnamecannotbeempty/backend-webtest.git']
                ]
        ])
    }

    stage("test") {
        sh 'sh ./mvnw clean package -Dmaven.test.failure.ignore=true -Dmaven.test.skip=true'
    }
    stage("publish") {
        publishHTML([
                allowMissing: false,
                alwaysLinkToLastBuild: false,
                keepAll: false,
                reportDir: 'target/site/jacoco',
                reportFiles: 'index.html',
                reportName: 'HTML Report',
                reportTitles: 'Coverage✨'
        ])
    }
    stage("docker-build") {
        sh "docker build -f Dockerfile -t se3app:latest ."
    }
    stage("restart") {
        try {
            sh 'docker rm -f se3'
        } catch(ignored){
            echo('Container\'s not running')
        }
        sh " docker run -d -p 9090:9090 -v /etc/localtime:/etc/localtime -v /etc/lucene/indexes:/etc/lucene/indexes --link se3mysql:se3mysql --name se3 se3app:latest"
    }
}