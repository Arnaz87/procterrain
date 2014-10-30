package me.arnaud.procterrain;

import com.jme3.app.SimpleApplication;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.material.RenderState.FaceCullMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;

/**
 * test
 * @author normenhansen
 */
public class Main extends SimpleApplication implements ActionListener {
    
    Geometry geom;
    DirectionalLight sun;
    TerrainManager terrain = new TerrainManager();
    boolean realmat = true;
    Coord before = new Coord(0,0,0);
    
    private FilterPostProcessor fpp;
    private FogFilter fog;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.setSettings(new AppSettings(true));
        app.settings.setFrameRate(30);
        app.showSettings = false;
        app.start();
        //app.prueba();
    }
    public void prueba () {
        for (int i = 0; i < 8; i++) {
            Coord lol = Coord.FromIndex(i, 2);
            System.out.println(lol);
        }
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(20);
        cam.setLocation(new Vector3f(5,15,5));
        cam.lookAtDirection(new Vector3f(1,0,1), Vector3f.UNIT_Y);
        attachCoordinateAxes(Vector3f.ZERO);
        Material mat;
        if (realmat) {
            mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            //mat.setBoolean("UseMaterialColors",true);
            mat.setColor("Diffuse", ColorRGBA.Gray );
            mat.setColor("Ambient", ColorRGBA.DarkGray );
            //mat.setBoolean("UseVertexColor", true);
        } else {
            mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", new ColorRGBA(1,1,1,.1f) );
            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
            mat.getAdditionalRenderState().setDepthWrite(false);
            //mat.setBoolean("VertexColor",true);
            mat.getAdditionalRenderState().setWireframe(true);
            mat.getAdditionalRenderState().setFaceCullMode(FaceCullMode.Off);
        }
        terrain.node = rootNode;
        terrain.mat = mat;
        Geometry geom2 = new Geometry("Box2", new Sphere(16,16,2));
        geom2.setMaterial(mat);
        rootNode.attachChild(geom2);
        
        sun = new DirectionalLight();
        sun.setDirection(new Vector3f(.5f,-2,-1).normalizeLocal());
        sun.setColor(new ColorRGBA(1,1,.8f,1));
        rootNode.addLight(sun);
        
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(.8f,.8f,1,1));
        rootNode.addLight(ambient);
        
        inputManager.addMapping("Refresh", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Refresh");
        
        fpp=new FilterPostProcessor(assetManager);
        //fpp.setNumSamples(4);
        System.out.println(fpp.getNumSamples());
        fog=new FogFilter();
        fog.setFogColor(new ColorRGBA(0.7f, 0.9f, 1.0f, 1.0f));
        fog.setFogDistance(200);
        fog.setFogDensity(1.5f);
        fpp.addFilter(fog);
        //if (realmat) viewPort.addProcessor(fpp);
        
        Refresh();
        
    }
    
    public void Refresh(){
        System.out.println("Refresh");
        terrain.ActualizePosition(new Coord(cam.getLocation().divide(16)));
    }

    public void onAction(String name, boolean value, float delta) {
        if (value && name.equals("Refresh")) {
            System.out.println("Click!");
            Refresh();
        }
    }
    
    private void attachCoordinateAxes(Vector3f pos){
      Arrow arrow = new Arrow(Vector3f.UNIT_X);
      arrow.setLineWidth(4); // make arrow thicker
      putShape(arrow, ColorRGBA.Red).setLocalTranslation(pos);

      arrow = new Arrow(Vector3f.UNIT_Y);
      arrow.setLineWidth(4); // make arrow thicker
      putShape(arrow, ColorRGBA.Green).setLocalTranslation(pos);

      arrow = new Arrow(Vector3f.UNIT_Z);
      arrow.setLineWidth(4); // make arrow thicker
      putShape(arrow, ColorRGBA.Blue).setLocalTranslation(pos);
    }

    private Geometry putShape(Mesh shape, ColorRGBA color){
      Geometry g = new Geometry("coordinate axis", shape);
      Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
      mat.getAdditionalRenderState().setWireframe(true);
      mat.setColor("Color", color);
      g.setMaterial(mat);
      g.setLocalScale(8);
      rootNode.attachChild(g);
      return g;
    }
    
    @Override
    public void simpleUpdate(float delta) {
        Coord actual = new Coord(cam.getLocation().divide(16));
        
        if (!actual.equals(before)){
            before = actual;
            //Refresh();
        }
    }
}
