package com.example.data.local

import android.content.Context
import com.example.data.model.CodeProject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectRepository(private val context: Context) {
    private val storageFile = File(context.filesDir, "lona_code_projects.json")
    private val _projects = MutableStateFlow<List<CodeProject>>(emptyList())
    val projects: StateFlow<List<CodeProject>> = _projects.asStateFlow()

    init {
        loadProjects()
    }

    private fun loadProjects() {
        try {
            if (!storageFile.exists()) {
                val defaultTemplates = createStarterTemplates()
                _projects.value = defaultTemplates
                saveToFile(defaultTemplates)
                return
            }
            val jsonStr = storageFile.readText()
            val array = JSONArray(jsonStr)
            val list = mutableListOf<CodeProject>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val id = obj.optString("id", "")
                val name = obj.optString("name", "Project")
                val desc = obj.optString("description", "")
                val ts = obj.optLong("createdAt", System.currentTimeMillis())
                val filesObj = obj.optJSONObject("files") ?: JSONObject()
                val filesMap = mutableMapOf<String, String>()
                val keys = filesObj.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    filesMap[k] = filesObj.optString(k, "")
                }
                list.add(CodeProject(id = id, name = name, description = desc, files = filesMap, createdAt = ts))
            }
            _projects.value = list
        } catch (e: Exception) {
            e.printStackTrace()
            _projects.value = createStarterTemplates()
        }
    }

    private fun saveToFile(list: List<CodeProject>) {
        try {
            val array = JSONArray()
            for (p in list) {
                val pObj = JSONObject()
                pObj.put("id", p.id)
                pObj.put("name", p.name)
                pObj.put("description", p.description)
                pObj.put("createdAt", p.createdAt)
                val fObj = JSONObject()
                for ((k, v) in p.files) {
                    fObj.put(k, v)
                }
                pObj.put("files", fObj)
                array.put(pObj)
            }
            storageFile.writeText(array.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveProject(project: CodeProject) = withContext(Dispatchers.IO) {
        val current = _projects.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            current[index] = project
        } else {
            current.add(0, project)
        }
        _projects.value = current
        saveToFile(current)
    }

    suspend fun deleteProject(id: String) = withContext(Dispatchers.IO) {
        val current = _projects.value.filter { it.id != id }
        _projects.value = current
        saveToFile(current)
    }

    private fun createStarterTemplates(): List<CodeProject> {
        val loginProject = CodeProject(
            id = "template_login",
            name = "Cyberpunk Neural Portal",
            description = "Futuristic dark neon login authentication screen with particle glow and interactive fields",
            files = mapOf(
                "index.html" to """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>LONA AI Neural Portal</title>
</head>
<body>
  <div class="hologram-card">
    <div class="orb-icon"></div>
    <h2>LONA AI // ACCESS</h2>
    <p class="subtitle">QUANTUM BIOMETRIC IDENTIFICATION</p>
    <div class="input-group">
      <input type="text" id="username" placeholder="OPERATOR IDENTIFIER" />
    </div>
    <div class="input-group">
      <input type="password" id="passkey" placeholder="NEURAL PASSKEY" />
    </div>
    <button id="authBtn" onclick="authorizeAccess()">SYNCHRONIZE LINK</button>
    <div id="statusLog" class="status-box">SYSTEM READY</div>
  </div>
</body>
</html>
                """.trimIndent(),
                "style.css" to """
body {
  margin: 0;
  padding: 0;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: radial-gradient(circle at center, #0a1128 0%, #030611 100%);
  font-family: 'Courier New', monospace;
  color: #00F5D4;
}
.hologram-card {
  width: 85%;
  max-width: 360px;
  background: rgba(10, 17, 40, 0.75);
  border: 1px solid #00F5D4;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 0 25px rgba(0, 245, 212, 0.25), inset 0 0 15px rgba(157, 78, 221, 0.2);
  text-align: center;
  backdrop-filter: blur(8px);
}
.orb-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 16px;
  border-radius: 50%;
  border: 2px solid #00F5D4;
  box-shadow: 0 0 15px #00F5D4, inset 0 0 15px #9D4EDD;
  animation: pulse 2s infinite alternate;
}
@keyframes pulse {
  0% { transform: scale(0.95); box-shadow: 0 0 10px #00F5D4; }
  100% { transform: scale(1.05); box-shadow: 0 0 25px #F72585; }
}
h2 {
  letter-spacing: 3px;
  margin: 0 0 4px;
  color: #fff;
}
.subtitle {
  font-size: 11px;
  color: #9D4EDD;
  margin-bottom: 20px;
}
.input-group input {
  width: 90%;
  padding: 12px;
  margin-bottom: 14px;
  background: rgba(5, 8, 20, 0.9);
  border: 1px solid #1C274C;
  border-radius: 8px;
  color: #00F5D4;
  font-family: inherit;
  outline: none;
  transition: all 0.3s;
}
.input-group input:focus {
  border-color: #00F5D4;
  box-shadow: 0 0 10px rgba(0, 245, 212, 0.4);
}
button {
  width: 100%;
  padding: 12px;
  background: linear-gradient(90deg, #00F5D4, #9D4EDD);
  border: none;
  border-radius: 8px;
  color: #000;
  font-weight: bold;
  cursor: pointer;
  letter-spacing: 2px;
  transition: transform 0.2s;
}
button:active {
  transform: scale(0.98);
}
.status-box {
  margin-top: 16px;
  font-size: 12px;
  color: #94A3B8;
}
                """.trimIndent(),
                "script.js" to """
function authorizeAccess() {
  const user = document.getElementById('username').value;
  const log = document.getElementById('statusLog');
  log.innerText = "AUTHENTICATING NEURAL SIG...";
  log.style.color = "#FFB703";
  setTimeout(() => {
    log.innerText = "ACCESS GRANTED // COMMANDER " + (user || "GUEST");
    log.style.color = "#00F5D4";
  }, 1000);
}
                """.trimIndent()
            )
        )

        val gameProject = CodeProject(
            id = "template_game",
            name = "Orbital Particle Canvas",
            description = "Interactive neon particle physics canvas responding to touch and mouse gestures",
            files = mapOf(
                "index.html" to """
<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>LONA Matrix Canvas</title>
</head>
<body>
  <div class="hud">TOUCH/DRAG TO EMIT QUANTUM PARTICLES</div>
  <canvas id="c"></canvas>
</body>
</html>
                """.trimIndent(),
                "style.css" to """
body, html {
  margin: 0;
  padding: 0;
  width: 100%;
  height: 100%;
  overflow: hidden;
  background: #030611;
}
canvas {
  display: block;
  width: 100%;
  height: 100%;
}
.hud {
  position: absolute;
  top: 16px;
  width: 100%;
  text-align: center;
  color: #00F5D4;
  font-family: monospace;
  font-size: 12px;
  pointer-events: none;
  letter-spacing: 2px;
}
                """.trimIndent(),
                "script.js" to """
const canvas = document.getElementById('c');
const ctx = canvas.getContext('2d');
canvas.width = window.innerWidth;
canvas.height = window.innerHeight;

let particles = [];
const colors = ['#00F5D4', '#9D4EDD', '#F72585', '#4CC9F0'];

function createParticle(x, y) {
  for (let i = 0; i < 5; i++) {
    particles.push({
      x: x,
      y: y,
      vx: (Math.random() - 0.5) * 6,
      vy: (Math.random() - 0.5) * 6,
      radius: Math.random() * 3 + 2,
      color: colors[Math.floor(Math.random() * colors.length)],
      alpha: 1.0
    });
  }
}

window.addEventListener('pointermove', (e) => {
  createParticle(e.clientX, e.clientY);
});
window.addEventListener('pointerdown', (e) => {
  createParticle(e.clientX, e.clientY);
});

function animate() {
  ctx.fillStyle = 'rgba(3, 6, 17, 0.2)';
  ctx.fillRect(0, 0, canvas.width, canvas.height);

  for (let i = particles.length - 1; i >= 0; i--) {
    const p = particles[i];
    p.x += p.vx;
    p.y += p.vy;
    p.alpha -= 0.02;

    if (p.alpha <= 0) {
      particles.splice(i, 1);
      continue;
    }

    ctx.save();
    ctx.globalAlpha = p.alpha;
    ctx.fillStyle = p.color;
    ctx.shadowBlur = 10;
    ctx.shadowColor = p.color;
    ctx.beginPath();
    ctx.arc(p.x, p.y, p.radius, 0, Math.PI * 2);
    ctx.fill();
    ctx.restore();
  }

  requestAnimationFrame(animate);
}
animate();
                """.trimIndent()
            )
        )

        return listOf(loginProject, gameProject)
    }
}
