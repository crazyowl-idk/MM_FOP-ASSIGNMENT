public class SaveLoad {

    GamePanel gp;

    public SaveLoad(GamePanel gp) {
        this.gp = gp;
}
    public Entity getObject(String itemName){
        Entity obj = null;

        switch(itemName){
            case "Name" : obj = new OBJ_AXE(gp); break;
            case "Boots" obj = new OBJ_Boots (gp); break;
        }
    }
    public void save() {
        try{
        ObjectOutputStream oos = new ObjectOutputStream (new FileOutputStream(new File("save.dat")));
        DataStorage ds = new DataStorage();

        //player stats
        ds.level = gp.player.level;
        ds.maxLife = gp.player.maxLife;
        ds.Life= gp.player.life;
        ds.maxMana = gp.player.maxMana;
        ds.mana = gp.player.mana;
        ds.strength = gp.player.strength;
        ds.dexterity = gp.player.desterity;
        ds.exp = gp.player.expl;
        ds.nextLevelExp = gp.player.nextLevelExp;
        ds.coin = gp.player.coin;
            // write data storage obj 
            //Player inventory 
            for (int i =0; i< gp.player.inventory.szie(); i++){
                ds.itemName.add(gp.player.inventory.get(i).name);
                ds.itemAmounts.add(gp.player.inventory.get(i).amount);
            }

            //Player equipment 
            ds.currentWeaponsSlot = gp.player.getCurrentWeaponSlot();
            ds.currentShieldSlot = gp.player.getCurrentShieldSlot();

            oos.writeObject(ds);
    }
        catch(Exception e){
            System.out.println("Save Exception!");
        }
    }

    public void load(){}

        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("save.dat")));

            //Read the DataStorage object 
            DataStorage ds = (DataStorage)ois.readObject();

 
            gp.player.level = ds.level;
            gp.player.maxLife = ds.maxLife;
            gp.player.life = ds.life;
            gp.player.maxMana = ds.maxMana;
            gp.player.strength = ds.strength;
            gp.plater.dexterity = ds.dexterity;
            gp.player.exp = ds.exp;
            gp.plater.nextLevelExp = ds.nextLevelExp; 
            gp.player.coin = ds.coin;

            // player inventory 
            gp.player.inventory.clear();
            for (int i=0; i< ds.itemName.size(); i++){
                gp.player.inventory.add(getObject(ds.itemNames.get(i)));
                gp.player.inventory.get(i).amount = ds.itemAmounts.get(i);
            }
            // Player Equipment 
            gp.player.currentWeapon = gp.player.inventory.get(ds.currentWeaponSlot);
            gp.player.currentShield = gp.player.inventory.get(ds.currentShieldSlot);
            gp.player.getAttack();
            gp.player.getDefense();
            gp.player.getAttackImage();


        catch(Exception e){
            System.out.println("Load Exception!");
            }
        }


// go to game panel map 
//SaveLoad saveLoad = new SaveLoad(this);

// go to life and mana
//gp.saveLoad.save();