#version 300 es

precision highp float;

in vec4 modelPosition;

out vec4 fragmentColor;

void main(void) {
  float w = fract(modelPosition.x);
  fragmentColor = mix(vec4(1,1,0,1), vec4(1,0,1,1), w);
}