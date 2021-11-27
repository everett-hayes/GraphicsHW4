import org.w3c.dom.HTMLCanvasElement
import org.khronos.webgl.WebGLRenderingContext as GL //# GL# we need this for the constants declared ˙HUN˙ a constansok miatt kell
import kotlin.js.Date
import vision.gears.webglmath.UniformProvider
import vision.gears.webglmath.Vec1
import vision.gears.webglmath.Vec2
import vision.gears.webglmath.Vec3
import vision.gears.webglmath.Vec4
import vision.gears.webglmath.Mat4
import kotlin.math.exp
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.cos
import kotlin.math.abs

class Scene (
  val gl : WebGL2RenderingContext)  : UniformProvider("scene") {

  // SHADERS HERE
  val vsTextured = Shader(gl, GL.VERTEX_SHADER, "textured-vs.glsl")
  val fsTextured = Shader(gl, GL.FRAGMENT_SHADER, "textured-fs.glsl")
  val texturedProgram = Program(gl, vsTextured, fsTextured)

  val vsBackground = Shader(gl, GL.VERTEX_SHADER, "background-vs.glsl")
  val fsBackground = Shader(gl, GL.FRAGMENT_SHADER, "background-fs.glsl")
  val backgroundProgram = Program(gl, vsBackground, fsBackground)

  val vsShadow = Shader(gl, GL.VERTEX_SHADER, "shadow-vs.glsl")
  val fsShadow = Shader(gl, GL.FRAGMENT_SHADER, "shadow-fs.glsl")
  val shadowProgram = Program(gl, vsShadow, fsShadow)

  val fsReflect = Shader(gl, GL.FRAGMENT_SHADER, "reflective-fs.glsl")
  val reflectiveProgram = Program(gl, vsTextured, fsReflect)

  val fsProcedure = Shader(gl, GL.FRAGMENT_SHADER, "procedural-fs.glsl")
  val proceduralProgram = Program(gl, vsTextured, fsProcedure)

  // GEOMETRIES HERE
  val texturedQuadGeometry = TexturedQuadGeometry(gl)
  val texturedQuadGeometry1 = TexturedQuadGeometry(gl)

  val envTexture = TextureCube(gl, 
    "media/posx512.jpg",
    "media/negx512.jpg",
    "media/posy512.jpg",
    "media/negy512.jpg",
    "media/posz512.jpg",
    "media/negz512.jpg",
  )

  val backgroundMaterial = Material(backgroundProgram).apply{
    this["envTexture"]?.set(envTexture)
  }

  val shadowMatrix by Mat4();

  init {
    shadowMatrix.set(shadowMatrix.scale(1.0f, 0.0f, 1.0f).translate(0.0f, 0.1f, 0.0f));
  }

  val shawdowMaterial = Material(shadowProgram).apply {
    this["shadowMatrix"]?.set(shadowMatrix)
  }

  // MESHES HERE
  val backgroundMesh = Mesh(backgroundMaterial, texturedQuadGeometry1)

  val jsonLoader = JsonLoader()

  val slowpokeMeshes = jsonLoader.loadMeshes(gl, 
    "media/slowpoke/slowpoke.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/slowpoke/YadonDh.png")
      )
    },
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/slowpoke/YadonEyeDh.png")
      )
    }
  )

  val chevy = jsonLoader.loadMeshes(gl, 
    "media/chevy/chassis.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/chevy/chevy.png")
      )
    }
  )

  val backRightWheel = jsonLoader.loadMeshes(gl, 
    "media/chevy/wheel.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/chevy/chevy.png")
      )
    }
  )

  val backLeftWheel = jsonLoader.loadMeshes(gl, 
    "media/chevy/wheel.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/chevy/chevy.png")
      )
    }
  )

  val frontRightWheel = jsonLoader.loadMeshes(gl, 
    "media/chevy/wheel.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/chevy/chevy.png")
      )
    }
  )

  val frontLeftWheel = jsonLoader.loadMeshes(gl, 
    "media/chevy/wheel.json",
    Material(texturedProgram).apply {
      this["colorTexture"]!!.set (
        Texture2D(gl, "media/chevy/chevy.png")
      )
    }
  )

  val slowpokeMeshes2 = jsonLoader.loadMeshes(gl, 
    "media/slowpoke/slowpoke.json",
    Material(reflectiveProgram).apply {
      this["colorTexture"]?.set (
        Texture2D(gl, "media/slowpoke/YadonDh.png")
      )
      this["envTexture"]?.set(envTexture)
    },
    Material(reflectiveProgram).apply {
      this["colorTexture"]?.set (
        Texture2D(gl, "media/slowpoke/YadonEyeDh.png")
      )
      this["envTexture"]?.set(envTexture)
    }
  )

  val slowpokeMeshes3 = jsonLoader.loadMeshes(gl, 
    "media/slowpoke/slowpoke.json",
    Material(proceduralProgram),
    Material(proceduralProgram)
  )

  // GAMEOBJECTS HERE
  val gameObjects = ArrayList<GameObject>()
  val avatar = object : GameObject(*chevy) {

    val acceleration = Vec3()
    
     override fun move (
      dt : Float,
      t : Float,
      keysPressed : Set<String>,
      gameObjects : List<GameObject>
      ) : Boolean {

        var drag : Float = 0.8f;

        acceleration.set(0f, -9.8f, 0f);
        if ("w" in keysPressed) {
          acceleration.set(Vec4(Vec3(0f, -9.8f, 12f), 0f) * modelMatrix);
        }

        if ("s" in keysPressed) {
          acceleration.set(Vec4(Vec3(0f, -9.8f, -12f), 0f) * modelMatrix);
        }

        if (" " in keysPressed && position.y == 0f) {
          velocity.y = 10f;
        }

        if ("q" in keysPressed && ("w" in keysPressed || "s" in keysPressed)) {
          yaw += 1f * dt;
        }

        if ("e" in keysPressed && ("w" in keysPressed || "s" in keysPressed)) {
          yaw -= 1f * dt;
        }

        velocity = velocity + (acceleration * dt);
        position += velocity * dt;

        velocity = velocity * exp(-drag * dt);

        if (position.y < 0f) position.y = 0f;

        return true
      }
  }

  val backRightWheelObj = GameObject(*backRightWheel).apply {
    noShadow = true;
    position.set(-8f,-1.8f,-11.2f)
    parent = avatar;
  }
  val backLeftWheelObj = GameObject(*backLeftWheel).apply{
    noShadow = true;
    position.set(8f,-1.8f,-11.2f)
    parent = avatar;
  }
  val frontRightWheelObj = GameObject(*frontRightWheel).apply{
    noShadow = true;
    position.set(-8f,-1.8f,13.3f)
    parent = avatar;
  }
  val frontLeftWheelObj = GameObject(*frontLeftWheel).apply {
    noShadow = true;
    position.set(8f,-1.8f,13.3f)
    parent = avatar;
  }

  init {

    gameObjects += GameObject(backgroundMesh).apply {
      noShadow = true;
    }
    gameObjects += avatar.apply {
      position.set(0f,15f,0f)
      noShadow = true;
      scale *= 0.7f;
    }
    gameObjects += GameObject(*slowpokeMeshes).apply {
      position.set(8f,0f,0f)
      yaw = 0.5f;
    }
    gameObjects += GameObject(*slowpokeMeshes2).apply {
      position.set(-10f,0f,0f)
      yaw = 1.7f;
    }
    gameObjects += GameObject(*slowpokeMeshes3).apply {
      position.set(0f,0f,30f)
      yaw = 2.7f;
    }
    gameObjects += backRightWheelObj;
    gameObjects += backLeftWheelObj;
    gameObjects += frontRightWheelObj;
    gameObjects += frontLeftWheelObj
  }

  val camera = PerspectiveCamera(*Program.all).apply {
    position.set(0f, 30f, -30f) // camera starting position
    yaw = 3.1415f
    pitch = -0.8f
    setAspectRatio(1.0f)
  }

  fun resize(canvas : HTMLCanvasElement) {
    gl.viewport(0, 0, canvas.width, canvas.height)//#viewport# tell the rasterizer which part of the canvas to draw to ˙HUN˙ a raszterizáló ide rajzoljon
    camera.setAspectRatio(canvas.width.toFloat()/canvas.height)
  }

  val timeAtFirstFrame = Date().getTime()
  var timeAtLastFrame =  timeAtFirstFrame

  init {
    gl.enable(GL.DEPTH_TEST)
    addComponentsAndGatherUniforms(*Program.all)
  }

  val epsilon = 0.2f;
  var isTurning = false;

  @Suppress("UNUSED_PARAMETER")
  fun update(keysPressed : Set<String>) {
    val timeAtThisFrame = Date().getTime() 
    val dt = (timeAtThisFrame - timeAtLastFrame).toFloat() / 1000.0f
    val t = (timeAtThisFrame - timeAtFirstFrame).toFloat() / 1000.0f
    timeAtLastFrame = timeAtThisFrame

    camera.move(dt, keysPressed);

    if (abs(avatar.velocity.x) > epsilon) {
      backRightWheelObj.pitch -= (avatar.velocity.x * dt);
      backLeftWheelObj.pitch += (avatar.velocity.x * dt);
      frontRightWheelObj.pitch -= (avatar.velocity.x * dt);
      frontLeftWheelObj.pitch += (avatar.velocity.x * dt);
    }

    if ("e" in keysPressed && !isTurning) {
      isTurning = true;
      frontLeftWheelObj.yaw -= 0.4f
      frontRightWheelObj.yaw -= 0.4f
    }

    if ("q" in keysPressed && !isTurning) {
      isTurning = true;
      frontLeftWheelObj.yaw += 0.4f
      frontRightWheelObj.yaw += 0.4f
    }

    if (!("e" in keysPressed || "q" in keysPressed) && isTurning) {
      isTurning = false;
      frontLeftWheelObj.yaw = 0f
      frontRightWheelObj.yaw = 0f
    }

    // if (avatar.velocity.x < (-1 * epsilon)) {
    //   backRightWheelObj.pitch -= (avatar.velocity.x * dt);
    //   backLeftWheelObj.pitch += (avatar.velocity.x * dt);
    //   frontRightWheelObj.pitch -= (avatar.velocity.x * dt);
    //   frontLeftWheelObj.pitch += (avatar.velocity.x * dt);
    // } else if (avatar.velocity.x > epsilon) {
    //   backRightWheelObj.pitch -= (avatar.velocity.x * dt);
    //   backLeftWheelObj.pitch += (avatar.velocity.x * dt);
    //   frontRightWheelObj.pitch -= (avatar.velocity.x * dt);
    //   frontLeftWheelObj.pitch += (avatar.velocity.x * dt);
    // }

    camera.position.set(avatar.position + Vec3(0f, 30f, -30f));
    
    gl.clearColor(0.3f, 0.0f, 0.3f, 1.0f)//## red, green, blue, alpha in [0, 1]
    gl.clearDepth(1.0f)//## will be useful in 3D ˙HUN˙ 3D-ben lesz hasznos
    gl.clear(GL.COLOR_BUFFER_BIT or GL.DEPTH_BUFFER_BIT)//#or# bitwise OR of flags

    gl.enable(GL.BLEND)
    gl.blendFunc(
      GL.SRC_ALPHA,
      GL.ONE_MINUS_SRC_ALPHA)

    gameObjects.forEach{ it.move(dt, t, keysPressed, gameObjects) }
    gameObjects.forEach{ it.update() }
    gameObjects.forEach{ it.draw(this, camera) }

    gameObjects.forEach{ 
      if (!it.noShadow) {
        it.using(shawdowMaterial).draw(this, this.camera);
      }
    }
  }
}
