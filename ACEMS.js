
module.exports = {
    apps: [{
          name: 'vue-app',
          script: 'server/index.js',
          instances: 1,
          autorestart: true,
          watch: false,
          exec_mode: 'fork',
          wait_ready: false,
          listen_timeout: false, // ready 시그널까지 기다릴 시간. 시간이 초과하면 앱을 강제로 재시작한다
          kill_timeout: false, // SIGINT 시그널 발생 후 SIGKILL 시그널까지 기다릴 시간. 시간이 초과하면 프로세스를 강제 종료한다.
          max_memory_restart: '500M',
          env: {
            NODE_ENV: 'production',
          }
        },
        {
            name: "frame-api",
            cwd: "./ems-ing/build/libs",
            args: [
                "-jar",
                "frame-api-0.0.1-SNAPSHOT-prod.jar" //여기에 war 파일 위치
            ],
            env: {
            },
            script: "C:/\"Program Files\"/Java/jdk-11.0.13/bin/java.exe",

            log_date_format: "YYYY-MM-DD HH:mm Z",
            exec_interpreter: "none",
            exec_mode: "fork"

        }]
}
