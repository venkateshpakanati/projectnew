
def workingdir = "/home/jenkins"

def clustername = "kubernetes"
def namespace = "tiller"

def artifactoryApiUri = null

podTemplate(cloud: clustername,
            namespace: namespace,
            containers: [
              containerTemplate(name: 'maven', image: 'maven:3.6.0-jdk-8-alpine',command: 'cat',
              envVars: [
                envVar(key: 'MAVEN_CONFIG', value: '${workingdir}/.m2')
              ],ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false),
              containerTemplate(name: 'occlient', image: 'openshift/origin-cli:latest',command: 'cat'
              ,ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false),
              containerTemplate(name: 'helm', image: 'venkateshpakanati/helm_push:1.3',command: 'cat'
              ,ttyEnabled: true,
             // workingDir: workingdir,
              alwaysPullImage: false)
            ],
            volumes: [
              configMapVolume(configMapName: "settings-xml", mountPath: '/home/jenkins/.m2'),
              emptyDirVolume(mountPath: '/home/groot/helm/repository/cache', memory: false)
            ]            
            ) {
                node(POD_LABEL) {
                    stage('Checkout Code') {
                        milestone ()
                        def myRepo = checkout scm
                        def gitCommit = myRepo.GIT_COMMIT
                        def gitLocalBranch = myRepo.GIT_LOCAL_BRANCH 
                        def gitPrevCommit = myRepo.GIT_PREVIOUS_COMMIT
                        def gitPrevSuccessCommit = myRepo.GIT_PREVIOUS_SUCCESSFUL_COMMIT
                        def gitBranch = myRepo.GIT_BRANCH
                        def shortGitCommit = "${gitCommit[0..10]}"
                        def previousGitCommit = sh(script: "git rev-parse ${gitCommit}~", returnStdout: true)
                        println "${gitCommit}   ${gitBranch}  ${shortGitCommit}  ${previousGitCommit}   ${gitLocalBranch}  ${gitPrevCommit} ${gitPrevSuccessCommit}"
                    }
                    stage('load properties') {
                        milestone()
                        if (env.BRANCH_NAME == 'develop') {
                          props = readProperties file:'jenkins-develop.properties'
                        } else if (env.BRANCH_NAME =~ /^(release|hotfix)/) {
                          props = readProperties file:'jenkins/jenkins-release.properties'
                        } else {
                          props = readProperties file:'jenkins-default.properties'
                        }  
                      
                        isBuildApp = props.build.toBoolean()
                        isPublishArtifacts = props.publishartifacts.toBoolean()
                        isDeploy = props.deploy.toBoolean()
                        artifactoryApiUri = props.artifactoryApiUri
                        isPublishArtifactsforRelease = props.isPublishArtifactsforRelease
                    }
                    if(isBuildApp) {
                        stage('Build a Maven project') {
                          milestone ()
                          container('maven') {
                            if(isPublishArtifactsforRelease) {
                                      def mavenPom = readMavenPom file: 'pom.xml'
                                      def pomVersion = mavenPom.properties['global.version']
                                      versionParts = pomVersion.split('\\.')
                                      def queryVersion = "${versionParts[0]}.${versionParts[1]}".toString()
                                      println "${pomVersion}  , ${queryVersion}"
                                      withCredentials([usernameColonPassword(credentialsId: 'cacheproject', variable: 'artifactoryUser')]) {
                                        sh  """
                                              touch aql.json
                                              echo 'items.find({"name":{"\$match":"$queryVersion*"},"repo":{"\$eq":"cacheproject"},"path":{"\$eq":"cacheproject"},"type":{"\$eq":"folder"}}).include("repo","path","name").sort({"\$desc":["name"]})' >> aql.json
                                              curl -X POST -o response.json --user "${artifactoryUser}" "${artifactoryApiUri}" -T aql-ver.json
                                            """
                                      }
                                      def responseFile = readFile 'response.json'
                                      def responseJson = readJSON text: "$responseFile"
                                      echo "Version Query Response JSON = $responseJson"
                                  
                                      sh 'ls -lrt && mvn -version'
                                      sh "mvn -V -B -U -T 8 clean deploy -s /home/jenkins/.m2/settings.xml"
                                }
                                else {  //build for develop in local jenkins workspace
                                  stage("build for develop") {
                                    milestone()
                                    sh "mvn -V -B -U -T 8 clean install -s /home/jenkins/.m2/settings.xml"
                                  }  
                                }
                            }
                        }
                    }   
                    if(isPublishArtifacts) {
                      stage('Build helm chart and publish helm chart') {
                          milestone()
                          container('helm') {
                            def chartPath = "projectchart"
                            def releasename ="projectchart";
                            def helmvirtualrepo = "local";
                            
                            sh '''
                              helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                              helm repo update --debug
                              helm repo list --debug
                            '''
                            sh "yq w -i projectchart/Chart.yaml version ${env.BUILD_ID}"
                            sh "yq w -i projectchart/Chart.yaml appVersion ${env.BUILD_ID}"
                            sh "yq w -i projectchart/values.yaml image.tag ${env.BUILD_ID}"
                                        
                            sh '''                                
                              chart_name="projectchart"
                              version=$(helm inspect "$chart_name" | yq r - 'version')
                              helm package projectchart
                              ls -lrt
                              chart_filename="${chart_name}-${version}.tgz"
                              curl -u admin:AP9YMHJpDaRrnUzzyY7e452G742 -X PUT -vvv -T "${chart_filename}" "http://172.42.42.104:8081/artifactory/helm/${chart_filename}"
                            '''
                            sh "yq w -i projectbuildchart/Chart.yaml version ${env.BUILD_ID}"
                            sh "yq w -i projectbuildchart/Chart.yaml appVersion ${env.BUILD_ID}"
                            sh "yq w -i projectbuildchart/values.yaml nginx_test.image.version ${env.BUILD_ID}"
                            sh "helm upgrade projectbuildchart projectbuildchart --tiller-namespace tiller --namespace tiller --install --force --debug"
                          }
                      }        
                      stage('Build docker image and publish image') {
                          milestone ()
                          container('occlient') {
                          sh """
                            ls -lrt
                            oc start-build projectbuildchart --from-dir . -n tiller
                          """
                          }
                      }
                    }
                    if(isDeploy) {
                      stage('Deploy') {
                          milestone()
                          container('helm') {
                            def helmchartAppVersion = '0.1.0'
                            sh '''
                              helm repo add helm http://172.42.42.104:8081/artifactory/helm --username admin --password AP9YMHJpDaRrnUzzyY7e452G742
                              helm repo update --debug
                            '''      
                            sh "helm upgrade projectchart http://172.42.42.104:8081/artifactory/helm/projectchart-${env.BUILD_ID}.tgz  --username admin --password AP9YMHJpDaRrnUzzyY7e452G742 --tiller-namespace tiller --namespace tiller --version ${env.BUILD_ID} --install --force --debug"
                      
                          }
                      }
                   } 
                }
}