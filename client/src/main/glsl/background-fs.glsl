#version 300 es

precision highp float;

in vec4 rayDir;
in vec4 texCoord;

uniform struct {
  samplerCube envTexture; 
} material;

out vec4 fragmentColor;

void main(void) {
  fragmentColor = texture(material.envTexture, rayDir.xyz); 
}