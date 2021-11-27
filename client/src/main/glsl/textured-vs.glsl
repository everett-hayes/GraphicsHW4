#version 300 es

in vec4 vertexPosition; //#vec4# A four-element vector [x,y,z,w].; We leave z and w alone.; They will be useful later for 3D graphics and transformations. #vertexPosition# attribute fetched from vertex buffer according to input layout spec
in vec4 vertexTexCoord;
in vec3 vertexNormal;

uniform struct{
  mat4 modelMatrix;
  mat4 modelMatrixInverse;
} gameObject;

uniform struct{
  mat4 viewProjMatrix; 
  vec3 position;
} camera;

uniform struct {
  float time;
} scene;

out vec4 modelPosition;
out vec4 worldPosition;
out vec4 texCoord;
out vec4 worldNormal;
out vec3 position;

void main(void) {
  modelPosition = vertexPosition;
  gl_Position = vertexPosition * gameObject.modelMatrix * camera.viewProjMatrix;
  worldPosition = gl_Position;
  texCoord = vertexTexCoord;
  worldNormal = gameObject.modelMatrixInverse * vec4(vertexNormal, 0);
}