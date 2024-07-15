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

float near = VeilCamera.NearPlane;
float far = VeilCamera.FarPlane;

in float vertexDistance;
//in vec4 vertexColor;
//in vec2 texCoord0;
in vec3 worldPos;
in vec3 rayDir;
in mat4 mats;
in vec2 texCoord;

out vec4 fragColor;


vec3 sphNormal( in vec3 pos, in vec4 sph ) {
    return normalize(pos-sph.xyz);
}

float linearize_depth(float d,float zNear,float zFar)
{
    float z_n = 2.0 * d - 1.0;
    return 2.0 * zNear * zFar / (zFar + zNear - z_n * (zFar - zNear));
}

// sphere of size ra centered at point ce
vec2 sphIntersect( in vec3 ro, in vec3 rd, in vec3 ce, float ra ) {
    vec3 oc = ro - ce;
    float b = dot( oc, rd );
    vec3 qc = oc - b*rd;
    float h = ra*ra - dot( qc, qc );
    if( h<0.0 ) return vec2(-1.0); // no intersection
    h = sqrt( h );
    return vec2( -b-h, -b+h );
}


void main() {
    //vec4 color = texture(Sampler0, vec2(1.0 - gl_FragCoord.x / ScreenSize.x, gl_FragCoord.y / ScreenSize.y)) * vertexColor * ColorModulator;

    vec2 sphereCenterDistance = sphIntersect(VeilCamera.CameraPosition, normalize(rayDir), spherePos + vec3(0.0, sphereSize , 0.0), sphereSize);
    //vec3 sp = sphereIntersect(spherePos, VeilCamera.CameraPosition, worldPos);
    if ( sphereCenterDistance.x < 0 && sphereCenterDistance.y < 0) {
        discard;
    }
    vec3 pos = VeilCamera.CameraPosition + sphereCenterDistance.y * normalize(rayDir);
    vec3 normal = sphNormal( pos, vec4(spherePos + vec3(0.0, sphereSize, 0.0), sphereSize));

    vec3 surfacePos = VeilCamera.CameraPosition + sphereCenterDistance.x * normalize(rayDir);

    float frensel = dot(normal, normalize(rayDir));

    vec3 depth_texture  =  texture(Sampler0, vec2(gl_FragCoord.x / ScreenSize.x, gl_FragCoord.y / ScreenSize.y)).xyz;

    vec4 clip_space_ground_pos = mats * vec4(surfacePos - spherePos, 1.0);

    gl_FragDepth = (clip_space_ground_pos.z / clip_space_ground_pos.w +1)/2;

    float terrainDistance = clamp(1 - abs(linearize_depth(gl_FragDepth, near, far) - linearize_depth(depth_texture.x, near, far)) * 3, 0, 1);


    //vec4 color = vec4(surfacePos, 1.0);
    //vec4 color = vec4(vec3(f_ndc_depth), 1.0);
    vec4 color = vec4(0.6, 0.6, 1.0, max(1.0 - frensel, terrainDistance) * opacity);
    //vec4 color = vec4(0.6, 0.6, 1.0, 0.4 * opacity);
    //fragColor = vec4(vec3(terrainDistance), 1f);
    fragColor = linear_fog(color, vertexDistance, FogStart, FogEnd, FogColor);
    //fragColor = vec4(vec3(linearize_depth(100, VeilCamera.NearPlane, VeilCamera.FarPlane)), 1.0);

    //fragColor = color;


}
