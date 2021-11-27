#version 300 es

precision highp float;

in vec4 texCoord;

uniform struct {
  sampler2D colorTexture; 
} material;

out vec4 fragmentColor;

void main(void) {
  // vec3 lightDir = normalize(vec3(1,1,1));
  // float cosa = clamp(dot(normal, lightDir), 0.0, 1.0);
  // fragmentColor = vec4(cosa * texture(material.colorTexture, texCoord.xy).rgb, 1); //* vec4(cosa, cosa, cosa, 1);
  fragmentColor = texture(material.colorTexture, texCoord.xy); 
}