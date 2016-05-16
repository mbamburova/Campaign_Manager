package campaignmanager.app;

import campaignmanager.Hero;
import campaignmanager.HeroManager;
import campaignmanager.HeroManagerImpl;

import javax.swing.table.AbstractTableModel;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;


/**
 * Created by Michaela Bamburová on 16.05.2016.
 */
public class HeroTableModel extends AbstractTableModel {

    private final HeroManager heroManager;
    private final ResourceBundle bundle;
    private List<Hero> heroList;

    public HeroTableModel() {
        heroManager = new HeroManagerImpl();  //?????
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        heroList = heroManager.findAllHeroes();
    }

    public List<Hero> getHeroList() {
        return Collections.unmodifiableList(heroList);
    }

    public void setHeroList(List<Hero> heroList) {
        this.heroList = heroList;
    }

    public void addHero(Hero hero) {
        heroManager.createHero(hero);
        heroList = heroManager.findAllHeroes();
    }

    public HeroManager getHeroManager() {
        return heroManager;
    }


    @Override
    public int getRowCount() {
        return heroList.size();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Hero hero = heroList.get(rowIndex);
        switch(columnIndex) {
            case 0:
                return hero.getId();
            case 1:
                return hero.getName();
            case 2:
                return hero.getLevel();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch(column) {
            case 0:
                return bundle.getString("Hero.id");
            case 1:
                return bundle.getString("Hero.name");
            case 2:
                return bundle.getString("Hero.level");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    public void removeRow(int row) {
       // heroManager.deleteHero(getValueAt(row, 0));
        heroList = heroManager.findAllHeroes();
    }

    public void update(Hero hero) {
        int i;
        for (i = 0; i < heroList.size() - 1; i++) {
            if (heroList.get(i).getId() == hero.getId()) {
                break;
            }
        }
        heroList.set(i, hero);
        fireTableRowsUpdated(i, i);
    }
}
