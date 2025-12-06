public class COnfig { // save config file

    GamePanel gp;

    public Config(GamePanem gp){
        this.gp = gp;
    }

    public void saveConfig(){
        BufferedWriter bw = new BufferedWriter(new FileWriter("config.txt"));

        //Full screen
        if(gp.fullScreenON == true){
            bw.write("On");
        }
        if (gp.fullScreenOFF == false){
            bw.write("Off");
        }
        bw.newLine();

        //Music voulume
        bw.write(String.valueOf(gp.music.volumeScale));
        bw.newLine();

        //SE volume 
        bw.write(String.valueof(gp.se.volumeScale));
        bw.newLine();

        bw.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void loadConfig(){}
        try { 
            BufferedReader br = new BufferedReader(new FileREader("config.txt"));

            String s = br.readLine();

            //Full screen 
            if(s.equal("On")){
                gp.fullScreenOn = true;
            }
            if (s.equals("Off")){
                gp.fullScreenOn = false;
            }

            //Music volume 
            s = br.readLine();
            gp.music.volumeScale = Integer.parseInt(s);
            
            //SE volume 
            s = br.readLine();
            gp.se.volumeScale = Integer.parseInt(s);
            
            br.close();

        } catch (FileNotFoundException e){
            e.printStackTrace();
        }
    }
}

//Game panel 
//Config config = new Config (this);

//Option tool
//gp.config.saveConfig();

//Gampanel
//gamePanel.config.loadConfig();
//if (gamePanel.fullScreenOn == true){
    window.setUndecorated (true);
}