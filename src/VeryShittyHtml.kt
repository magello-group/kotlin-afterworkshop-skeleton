import kotlinx.html.*
import java.net.NetworkInterface

// IP for wifi, shitty list for hardcoded badness.
val ip = NetworkInterface.getByName("wlp61s0")?.inetAddresses?.toList()?.last()?.toString()?.replace("/", "")
    ?: NetworkInterface.getByName("enp0s31f6")?.inetAddresses?.toList()?.last()?.toString()?.replace("/", "")
    ?: NetworkInterface.getByName("enx9cebe84474d4")?.inetAddresses?.toList()?.last()?.toString()?.replace("/", "")
    ?: "localhost"

val shittyHtml: HTML.() -> Unit = {
    head {
        link {
            rel = "stylesheet"
            href = "static/css/bootstrap.css"
        }
        script {
            src = "static/js/jquery-3.3.1.slim.min.js"
        }
        script {
            src = "static/js/popper.min.js"
        }
        script {
            src = "static/js/bootstrap.min.js"
        }
    }
    body {
        table {
            tr {
                td {
                    img {
                        src = "static/kotlin-logo.svg"
                        height = "200px"
                        width = "200px"
                    }
                }
                td {
                    h1 {
                        id = "time"
                        text("")
                    }
                    h1 {
                        id = "ip"
                        text("Post to: http://${ip}:8080/score")
                    }
                    h1 {
                        id = "robin"
                        text("RobinHood is Ready!")
                    }
                }
            }
        }
        table(classes = "table table-striped table-dark") {
            id = "table"
            tr {
                th { +"#" }
                th { +"Score" }
                th { +"Name" }
            }
            repeat(50) {
                tr {
                    td {
                        +"${it + 1}"
                    }
                    td {
                        id = "$it@score"
                    }
                    td {
                        id = "$it@name"
                    }
                }
            }
        }
        script {
            unsafe {
                +"""
let socket = new WebSocket('ws://${ip}:8080/myws/echo');
socket.onopen = function(e) {
  console.log('[open] Connection established');
};

socket.onmessage = function(event) {
  const data = JSON.parse(event.data);
  document.getElementById('time').innerText = data.date;
  const length = data.highScore.length;
  const loopSize = Math.min(length, 50);
  for( let i = 0; i < loopSize; i++ ) {
    document.getElementById(`${'$'}{i}@name`).innerText = data.highScore[i].name;
    document.getElementById(`${'$'}{i}@score`).innerText = data.highScore[i].score;
  }
  if(data.robinReady) {
    document.getElementById('robin').innerText = 'RobinHood is ready!';
  } else {
    document.getElementById('robin').innerText = 'RobinHood is not ready yet...';
  }
};

socket.onclose = function(event) {
  if (event.wasClean) {
    console.log(`[close] Connection closed cleanly, code=${'$'}{event.code} reason=${'$'}{event.reason}`);
  } else {
    // e.g. server process killed or network down
    // event.code is usually 1006 in this case
    console.log('[close] Connection died');
  }
};

socket.onerror = function(error) {
  alert(`[error] ${'$'}{error.message}`);
};
"""
            }
        }
    }
}
