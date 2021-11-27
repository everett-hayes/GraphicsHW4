#version 300 es

in vec4 vertexPosition;
in vec4 vertexTexCoord;

uniform struct {
  mat4 rayDirMatrix; 
} camera;

out vec4 rayDir;
out vec4 textCoord;

void main(void) {
    rayDir =  vertexPosition * camera.rayDirMatrix;
    textCoord = vertexTexCoord;
    gl_Position = vertexPosition;
    gl_Position.z = .99999;
}



