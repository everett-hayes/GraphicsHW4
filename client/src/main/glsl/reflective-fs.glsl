#version 300 es

precision highp float;

in vec4 texCoord;
in vec4 worldNormal;
in vec3 position;
in vec4 worldPosition;

uniform struct {
  sampler2D colorTexture; 
  samplerCube envTexture; 
} material;

out vec4 fragmentColor;

void main(void) {
  vec3 normal = normalize(worldNormal.xyz);
  vec3 viewDir = normalize(position.xyz - worldPosition.xyz);
  fragmentColor = texture(material.envTexture, reflect(-viewDir, normal)); //* vec4(cosa, cosa, cosa, 1);
}