#include veil:fog
#include veil:camera

uniform sampler2D Sampler0;

uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;
uniform vec2 ScreenSize;
uniform vec3 spherePos;
uniform float sphereSize;
uniform float opacity;
uniform mat4 modelViewMat;
uniform mat4 ProjMat;

in float vertexDistance;
//in vec4 vertexColor;
//in vec2 texCoord0;
in vec3 worldPos;
in vec3 rayDir;

out vec4 fragColor;


vec3 sphNormal( in vec3 pos, in vec4 sph )
{
    return normalize(pos-sph.xyz);
}

// sphere of size ra centered at point ce
vec2 sphIntersect( in vec3 ro, in vec3 rd, in vec3 ce, float ra )
{
    vec3 oc = ro - ce;
    float b = dot( oc, rd );
    vec3 qc = oc - b*rd;
    float h = ra*ra - dot( qc, qc );
    if( h<0.0 ) return vec2(-1.0); // no intersection
    h = sqrt( h );
    return vec2( -b-h, -b+h );
}
/*
vec3 sphereIntersect(vec3 c, vec3 ro, vec3 p) {
    float sphereScale = 0.1;

    vec3 rd = vec3(normalize(p - ro)) / vec3(sphereScale);;
    vec3 u = vec3(ro - c) / vec3(sphereScale);; // ro relative to c

    float a = dot(rd, rd);
    float b = 2.0 * dot(u, rd);
    float cc = dot(u, u) - 1.0;

    float discriminant = b * b - 4 * a * cc;

    // no intersection
    if (discriminant < 0.0) {
        return vec3(0.0);
    }

    float t1 = (-b + sqrt(discriminant)) / (2.0 * a);
    float t2 = (-b - sqrt(discriminant)) / (2.0 * a);
    float t = min(t1, t2);
    vec3 intersection = ro + vec3(t * rd) * sphereScale;

    return intersection;
}
*/

void main() {
    //vec4 color = texture(Sampler0, vec2(1.0 - gl_FragCoord.x / ScreenSize.x, gl_FragCoord.y / ScreenSize.y)) * vertexColor * ColorModulator;

    vec2 sphereCenterDistance = sphIntersect(VeilCamera.CameraPosition, normalize(-rayDir), spherePos + vec3(0.0, sphereSize , 0.0), sphereSize);
    //vec3 sp = sphereIntersect(spherePos, VeilCamera.CameraPosition, worldPos);
    if ( sphereCenterDistance.x < 0 && sphereCenterDistance.y < 0) {
        discard;
    }
    vec3 pos = VeilCamera.CameraPosition + sphereCenterDistance.y * normalize(-rayDir);
    vec3 normal = sphNormal( pos, vec4(spherePos + vec3(0.0, sphereSize, 0.0), sphereSize));

    vec3 surfacePos = VeilCamera.CameraPosition + sphereCenterDistance.x * normalize(-rayDir);

    float frensel = dot(normal, normalize(-rayDir));

    float depth_texture  =  texture(Sampler0, vec2(gl_FragCoord.x / ScreenSize.x, gl_FragCoord.y / ScreenSize.y)).x;


    //vec4 color = vec4(surfacePos, 1.0);
    //vec4 color = vec4(vec3(f_ndc_depth), 1.0);
    vec4 color = vec4(0.6, 0.6, 1.0, (1.0 - frensel) * opacity);
    //vec4 color = vec4(0.6, 0.6, 1.0, 0.4 * opacity);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
    //fragColor = color;


}
