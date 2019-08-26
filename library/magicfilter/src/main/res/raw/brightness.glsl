#extension GL_OES_EGL_image_external : require

varying highp vec2 textureCoordinate;

uniform samplerExternalOES inputImageTexture;
uniform lowp float brightness;

void main() {
    lowp vec4 textureColor = texture2D(inputImageTexture, textureCoordinate);

    gl_FragColor = vec4((textureColor.rgb + vec3(brightness)), textureColor.w);
}