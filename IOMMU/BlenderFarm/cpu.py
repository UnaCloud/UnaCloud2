import bpy
import sys

start = int(sys.argv[-2])
end = int(sys.argv[-1])

bpy.context.user_preferences.system.compute_device_type = 'CUDA'
bpy.context.user_preferences.system.compute_device = 'CUDA_0'
bpy.context.scene.cycles.device = 'CPU'

sce = bpy.context.scene
sce.render.tile_x = 256
sce.render.tile_x = 256
while start <= end:
	sce.frame_current = start
	sce.render.filepath='/media/blender/cpu_resultset_256/' + str(start)
	start = start + 1
	bpy.ops.render.render(write_still=True)
