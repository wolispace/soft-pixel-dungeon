package com.shatteredpixel.shatteredpixeldungeon.scenes;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.utils.Bundle;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.GamesInProgress;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.ui.Archs;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndOptions;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndStory;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.Utils;

import android.annotation.SuppressLint;
import android.os.Environment;

@SuppressLint("DefaultLocale")
public class LoadSaveScene extends PixelScene {

	private static final float BUTTON1_WIDTH = 34;
	private static final float BUTTON2_WIDTH = 55;
	private static final float BUTTON_HEIGHT = 20;
	private static final float BUTTON_PADDING = 3;

	private static final String TXT_TITLE	= "Save/Load ";
	
	private static final String GAME_FOLDER	= "SoftPixelDungeon";
	private static final String SAVE_FOLDER	= "saves";	
	private static final String SD_ROOT = Environment.getExternalStorageDirectory().toString();

	private static final String TXT_LOAD	= "Load";
	private static final String TXT_SAVE	= "Save";
	private static final String TXT_SLOTNAME= "Game";

	private static final String HERO		= "hero";
	private static final String DEPTH		= "depth";
	private static final String LEVEL		= "lvl";
	
    private static final String TXT_REALLY	= "Load";
    private static final String TXT_WARNING	= "Your current progress will be lost.";
    private static final String TXT_YES		= "Yes, load " + TXT_SLOTNAME;
    private static final String TXT_NO		= "No, return to main menu";
    
    private static final String TXT_DPTH_LVL	= "Depth: %d, level: %d";
	
	
	private static final int CLR_WHITE	= 0xFFFFFF;
	
    public static HeroClass curClass;
		
	
	@Override
	public void create() {
		
		super.create();
		
		makeFolder(SD_ROOT + "/" + GAME_FOLDER);
		makeFolder(SD_ROOT + "/" + GAME_FOLDER + "/" + SAVE_FOLDER);
		
		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs );
		
		String showClassName = capitalizeWord(Dungeon.hero.heroClass.title());
		
		BitmapText title = PixelScene.createText( TXT_TITLE + showClassName, 9 );
		title.hardlight( Window.TITLE_COLOR );
		title.measure();
		title.x = align( (w - title.width()) / 2 );
		title.y = BUTTON_PADDING;
		add( title );

        String currentProgress = "dead";
		GamesInProgress.Info info = GamesInProgress.check( Dungeon.hero.heroClass );
        if (info != null) {
            currentProgress = Utils.format( TXT_DPTH_LVL, info.depth, info.level );
        }
		BitmapText subTitle = PixelScene.createText( "Currently " + currentProgress, 6 );
		subTitle.hardlight( Window.TITLE_COLOR );
		subTitle.measure();
		subTitle.x = align( (w - title.width()) / 2 );
		subTitle.y =  (BUTTON_HEIGHT / 2) + BUTTON_PADDING ;
		add( subTitle );
		
		int posY = (int) BUTTON_HEIGHT + ( (int) BUTTON_PADDING * 3);
		int posX2 = w - (int) (BUTTON2_WIDTH + BUTTON_PADDING);
		int posX = (int) (BUTTON1_WIDTH + (BUTTON_PADDING * 3));
		
		// rogue and huntress do not have matching file names so we need to specify each..
        String[] classList = { "warrior", "mage", "rogue,game,depth", "huntress,ranger,ranger" };
        String[] slotList = { "A", "B", "C", "D", "E", "F", "G", "H" };

		for (String classInfo : classList) {
			String className = getClassInfo(classInfo, "className");
			String test = Dungeon.hero.heroClass.title();
			if (Dungeon.hero.heroClass.title().equals(className)) {
				
				for (String saveSlot : slotList) {
					// add the row caption..
					BitmapText buttonCapton1 = PixelScene.createText( TXT_SLOTNAME + " " + saveSlot, 9 );
					buttonCapton1.hardlight( CLR_WHITE );
					buttonCapton1.measure();
					buttonCapton1.x = BUTTON_PADDING;
					buttonCapton1.y = posY + (BUTTON_HEIGHT/3);
					add( buttonCapton1 );
	
					// add the save button..
					if (Dungeon.hero.isAlive()) {
						GameButton btnSave = new GameButton( this, true, TXT_SAVE, "", classInfo, saveSlot );
						add( btnSave );
						btnSave.visible = true;
						btnSave.setRect(posX, posY, BUTTON1_WIDTH, BUTTON_HEIGHT);	
					}				
					// add the load button if there are saved files to load..
					String saveSlotFolder = SD_ROOT + "/" + GAME_FOLDER + "/" + SAVE_FOLDER + "/" + className + saveSlot;
					File backupFolder = new File(saveSlotFolder);
					if (backupFolder.exists()) {
						FileInputStream input;
						try {
							String classFile = getClassInfo(classInfo, "classFile");
							input = new FileInputStream(saveSlotFolder +"/" + classFile +".dat");
							Bundle bundle = Bundle.read( input );
							input.close();
							int savedDepth = bundle.getInt( DEPTH );
							Bundle savedHero = bundle.getBundle( HERO );
							int savedLevel = savedHero.getInt( LEVEL );				
	                        String savedProgress = Utils.format( TXT_DPTH_LVL, savedDepth, savedLevel );
							GameButton btnLoad1A = new GameButton( this, false, TXT_LOAD , savedProgress, classInfo, saveSlot );
	
							add( btnLoad1A );
							btnLoad1A.visible = true;
							btnLoad1A.setRect(posX2, posY, (int) (BUTTON2_WIDTH), BUTTON_HEIGHT);
						} catch (FileNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
					}
					// move down the line now...
					posY += BUTTON_HEIGHT + BUTTON_PADDING;
				}
			}			
		}		
		
		fadeIn();	
	}
	
	@Override
	protected void onBackPressed() {
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
        Game.switchScene( InterlevelScene.class );		
	}

	// returns the named element from the string 
	private static String getClassInfo(String classInfo, String fieldName) {
		String[] classBits = classInfo.split(",");
		String returnValue = classBits[0];
		if ("classFile".equals(fieldName) && classBits.length > 1) {
			returnValue = classBits[1];
		}
		if ("levelFile".equals(fieldName) && classBits.length > 2) {
			returnValue = classBits[2];
		}
		return returnValue;
	}
    protected static void exportGames(String classInfo, String saveSlot) {	
		ArrayList<String> files = new ArrayList<String>();
		String savePath = SD_ROOT + "/" + GAME_FOLDER + "/" + SAVE_FOLDER;
		
		String className = getClassInfo(classInfo, "className");
				
		// make a folder like /cdcard/softPixelDungeon/saves/warrior1/
		String saveSlotFolder = savePath + "/" + className + saveSlot;
		makeFolder(saveSlotFolder);
		
		for(String fileName : Game.instance.fileList()){
			if(isGameLevelFile(classInfo, fileName)){
				files.add(fileName);
			}
		}
		
		// remove previous saved game files..
		File backupFolder = new File(saveSlotFolder);
		
		for(File backupFile : backupFolder.listFiles()){
			if(isGameLevelFile(classInfo, backupFile.getName())){
				backupFile.delete();
			}
		}		

		for (String fileName : files){
			try {
				FileInputStream in = Game.instance.openFileInput(fileName);
			    OutputStream out = new FileOutputStream(saveSlotFolder + "/" + fileName);

			    // Transfer bytes from in to out
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			}
			catch(Exception e){
				e.printStackTrace();
				WndStory.showChapter("Failed to save file " + fileName);
				//Log.d("FAILED EXPORT", f);
			}
		}
		InterlevelScene.mode = InterlevelScene.Mode.SAVE;
		Game.switchScene( InterlevelScene.class );
		//WndStory.showChapter(playerClass + " " + Integer.toString(saveSlot) + " saved");
	}

	private static boolean isGameLevelFile(String classInfo, String fileName) {
		String classFile = getClassInfo(classInfo, "classFile");
		String levelFile = getClassInfo(classInfo, "levelFile");
		return fileName.endsWith(".dat") && (fileName.startsWith(classFile) || fileName.startsWith(levelFile));
	}

	private static void makeFolder(String saveSlotFolder) {
		File dir = new File(saveSlotFolder);
		if (!dir.exists()) {
			dir.mkdir();
		}
	}

    protected static void importGames(String classInfo, String saveSlot) {	
		ArrayList<String> files = new ArrayList<String>();
		String savePath = SD_ROOT + "/" + GAME_FOLDER + "/" + SAVE_FOLDER;
		String className = getClassInfo(classInfo, "className");
		String saveSlotFolder = savePath + "/" + className + saveSlot;
		File backupFolder = new File(saveSlotFolder);
		
		for(File backupFile : backupFolder.listFiles()){
			if(isGameLevelFile(classInfo, backupFile.getName())){
				files.add(backupFile.getName());
			}
		}

		// remove in progress game files..
		for(String fileName : Game.instance.fileList()){
			if(fileName.startsWith("game_") || isGameLevelFile(classInfo, fileName)){
				Game.instance.deleteFile(fileName);
			}
		}
		
		
		for (String fileName : files){
			try {
				FileInputStream in = new FileInputStream(saveSlotFolder + "/" + fileName); //
			    OutputStream out = Game.instance.openFileOutput(fileName, Game.MODE_PRIVATE );

			    // Transfer bytes from in to out
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = in.read(buf)) > 0) {
			        out.write(buf, 0, len);
			    }
			    in.close();
			    out.close();
			}
			catch(Exception e){
				e.printStackTrace();
				WndStory.showChapter("Failed to load file " + fileName);
				//Log.d("FAILED EXPORT", f);
			}
		}
        InterlevelScene.mode = InterlevelScene.Mode.CONTINUE;
        Game.switchScene( InterlevelScene.class );
    }
 
    private static class GameButton extends RedButton {
		
		private static final int SECONDARY_COLOR	= 0xCACFC2;
		
		private BitmapText secondary;
		private Boolean isSave = true;
		private String classInfo = "";
		private String saveSlot = "";
		private LoadSaveScene loadSaveScene;
		
		public GameButton(LoadSaveScene loadSaveScene, Boolean isSave, String primary, String secondary, String classInfo, String saveSlot ) {
			super( primary );
			this.secondary( secondary );
			this.isSave = isSave;
			this.classInfo = classInfo;
			this.saveSlot = saveSlot;
			this.loadSaveScene = loadSaveScene;
		}
		@Override
		protected void onClick() {
			if (isSave) {
				exportGames(classInfo, saveSlot);
			} else {
				loadSaveScene.add( new WndOptions( TXT_REALLY + " " +saveSlot + " " + secondary.text() + "?", TXT_WARNING, TXT_YES + " " + saveSlot, TXT_NO ) {
                    @Override
                    protected void onSelect( int index ) {
                        if (index == 0) {
            				importGames(classInfo, saveSlot);
                        }
                    }
                } );
			}
		};
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			secondary = createText( 6 );
			secondary.hardlight( SECONDARY_COLOR );
			add( secondary );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			if (secondary.text().length() > 0) {
				text.y = y + (height - text.height() - secondary.baseLine()) / 2;
				
				secondary.x = align( x + (width - secondary.width()) / 2 );
				secondary.y = align( text.y + text.height() ); 
			} else {
				text.y = y + (height - text.baseLine()) / 2;
			}
		}
		
		public void secondary( String text ) {
			secondary.text( text );
			secondary.measure();
		}

	}
    public static String capitalizeWord(String oneWord)
    {
      return Character.toUpperCase(oneWord.charAt(0)) + oneWord.substring(1);
    }
}
