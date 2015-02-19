Graphics3D 1366, 768, 32, 1

SetBuffer BackBuffer()

camera=CreateCamera()
EntityRadius camera, 1.5
CameraFogColor camera, 0, 40, 80
CameraFogRange camera, 5, 30

light=CreateLight()

skybox = MakeSkyBox("sky")

TurnEntity light, 90, 0, 0

terrain=LoadTerrain( "mesa_heightmap.png" )
TerrainDetail terrain,40000,True
ScaleEntity terrain,1,50,1
TerrainShading terrain, True
grass_tex=LoadTexture( "texture.jpg" )
ScaleTexture grass_tex, 3, 3
EntityTexture terrain,grass_tex,0,1

tree=LoadMesh("rallycar1.3ds")
ScaleEntity tree, 0.02,0.02,0.02
PositionEntity tree, 100, TerrainHeight(terrain, 100, 100) * 50, 100

SeedRnd (MilliSecs())
For i = 1 To 50
	newtree = CopyEntity(tree)
	x = Rand(0, 512)
	y = Rand(0, 512)
	PositionEntity newtree, x, TerrainHeight(terrain, x, y) * 50, y
	EntityType newtree, 2
Next
FreeEntity tree

water = CreatePlane()
PositionEntity water, 0, 10, 0
water_tex=LoadTexture( "water2.bmp" )
ScaleTexture water_tex, 5, 5
EntityTexture water,water_tex,0,1
EntityAlpha water, 0.7
EntityFX water, 16

PositionEntity camera, 200, 20, 50

frameTimer=CreateTimer(30)
wireframetoggle = False

EntityType camera, 1
EntityType terrain, 2

Collisions 1, 2, 2, 3
;Collisions 1, 3, 

pitch# = 0
yaw# = 0
ripple# = 0
yvel# = 0

HidePointer

While Not KeyHit(1)

	WaitTimer(frameTimer)
	
	yaw = yaw + (GraphicsWidth() / 2 - MouseX()) * 0.3
	pitch = pitch - (GraphicsHeight() / 2 - MouseY()) * 0.2
	
	If pitch > 90 Then
		pitch = 90
	EndIf
	If pitch < -90 Then
		pitch = -90
	EndIf
		
	RotateEntity camera, pitch, yaw, 0

	
	xco# = 0
	yco# = 0

	
	If KeyDown(31) Then
		yco = yco - 1
	EndIf
	
	If KeyDown(17) Then
		yco = yco + 1
	EndIf
	
	If KeyDown(30) Then
		xco = xco - 1
	EndIf
	
	If KeyDown(32) Then
		xco = xco + 1
	EndIf
	
	If xco <> 0 Or yco <> 0
		TranslateEntity camera, Cos(ATan2(yco, xco) + yaw) * 0.2, 0, Sin(ATan2(yco, xco) + yaw) * 0.2
	EndIf
	
	If KeyHit(28) Then
		wireframetoggle = Not wireframetoggle
		WireFrame wireframetoggle
	EndIf
	
	yvel = yvel - 0.02
	
	If EntityY(camera) < 10 Then
		yvel = yvel + 0.04
		yvel = yvel * 0.98
		CameraFogMode camera, 1
	Else
		CameraFogMode camera, 0
	EndIf
	
	If EntityCollided(camera, 2) Then
		yvel = 0
				
		If KeyHit(57) Then
			yvel = 0.3
		EndIf
	EndIf
	
	yvel = yvel * 0.98
	
	TranslateEntity camera, 0, yvel, 0
	
	ripple = ripple + 1
	PositionEntity water, Sin(ripple), 10 + Sin(ripple / 2) / 2, Cos(ripple)
		
	PositionEntity skybox,EntityX(camera,1),EntityY(camera,1),EntityZ(camera,1)

	MoveMouse GraphicsWidth() / 2, GraphicsHeight() / 2

	UpdateWorld
	RenderWorld
	
	Flip

Wend
End

Function MakeSkyBox( file$ )

	m=CreateMesh()
	;front face
	b=LoadBrush( file$+"_FR.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,-1,0,0:AddVertex s,+1,+1,-1,1,0
	AddVertex s,+1,-1,-1,1,1:AddVertex s,-1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;right face
	b=LoadBrush( file$+"_LF.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,-1,0,0:AddVertex s,+1,+1,+1,1,0
	AddVertex s,+1,-1,+1,1,1:AddVertex s,+1,-1,-1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;back face
	b=LoadBrush( file$+"_BK.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,+1,+1,+1,0,0:AddVertex s,-1,+1,+1,1,0
	AddVertex s,-1,-1,+1,1,1:AddVertex s,+1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;left face
	b=LoadBrush( file$+"_RT.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,0:AddVertex s,-1,+1,-1,1,0
	AddVertex s,-1,-1,-1,1,1:AddVertex s,-1,-1,+1,0,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b
	;top face
	b=LoadBrush( file$+"_UP.bmp",49 )
	s=CreateSurface( m,b )
	AddVertex s,-1,+1,+1,0,1:AddVertex s,+1,+1,+1,0,0
	AddVertex s,+1,+1,-1,1,0:AddVertex s,-1,+1,-1,1,1
	AddTriangle s,0,1,2:AddTriangle s,0,2,3
	FreeBrush b

	ScaleMesh m,100,100,100
	FlipMesh m
	EntityFX m,9
	EntityOrder m,10
	Return m
	
End Function