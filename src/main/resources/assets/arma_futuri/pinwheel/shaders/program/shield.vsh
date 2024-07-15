#include veil:light
#include veil:fog
#include veil:camera

//layout(location = 1) in vec4 SpherePos;
//layout(location = 2) in vec2 UV0;
//layout(location = 3) in ivec2 UV2;

//uniform mat4 ModelViewMat;
uniform mat4 modelViewMat;
uniform mat4 ProjMat;
uniform vec3 ChunkOffset;
uniform int FogShape;
uniform vec3 spherePos;

out float vertexDistance;
out vec3 worldPos;
out vec3 rayDir;
out mat4 mats;
out vec2 texCoord;
layout(location = 0) in vec3 Position;


void main() {
    worldPos = Position;
    rayDir = (worldPos + spherePos) - VeilCamera.CameraPosition;
    mats = ProjMat * modelViewMat;

    vertexDistance = fog_distance(modelViewMat, Position, FogShape);

    gl_Position = mats * vec4(Position, 1.0f);
}