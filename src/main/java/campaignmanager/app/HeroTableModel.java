package campaignmanager.app;

import campaignmanager.CampaignManager;
import campaignmanager.Hero;
import campaignmanager.HeroManager;
import campaignmanager.Mission;
import campaignmanager.common.ValidationException;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;


/**
 * Created by Michaela Bamburová on 16.05.2016.
 */
public class HeroTableModel extends AbstractTableModel {

    private final HeroManager heroManager;
    private final CampaignManager campaignManager;
    private final ResourceBundle bundle;
    private List<Hero> heroList = new ArrayList<>();
    private ReadAllSwingWorker readWorker;
    final static org.slf4j.Logger log = LoggerFactory.getLogger(HeroTableModel.class);
    private JOptionPane dialog;


    public HeroTableModel(HeroManager heroManager, CampaignManager campaignManager) {
        this.heroManager = heroManager;
        this.campaignManager = campaignManager;
        bundle = ResourceBundle.getBundle("Bundle", Locale.getDefault());
        readWorker = new ReadAllSwingWorker(heroManager);
        readWorker.execute();
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
        switch (columnIndex) {
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

        switch (column) {
            case 0:
                return bundle.getString("ID");
            case 1:
                return bundle.getString("NAME");
            case 2:
                return bundle.getString("LEVEL");
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    private class ReadAllSwingWorker extends SwingWorker<List<Hero>,Void> {
        private final HeroManager heroManager;

        public ReadAllSwingWorker(HeroManager manager) {
            heroManager = manager;
        }

        @Override
        protected List<Hero> doInBackground() throws Exception {
            return heroManager.findAllHeroes();
        }

        @Override
        protected void done() {
            try {
                heroList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
            }
        }
    }

    private class FilterSwingWorker extends SwingWorker<List<Hero>, Void> {

        private final HeroManager heroManager;
        private final CampaignManager campaignManager;
        private Object object;
        private final int filterType;

        public FilterSwingWorker(HeroManager heroManager, CampaignManager campaignManager, Object object, int filterType) {
            this.heroManager = heroManager;
            this.campaignManager = campaignManager;
            this.object = object;
            this.filterType = filterType;
        }

        @Override
        protected List<Hero> doInBackground() throws Exception {
            switch (filterType) {
                case 0:
                    return heroManager.findAllHeroes();
                case 1:
                    return heroManager.viewFreeHeroes();
                case 2:
                    return heroManager.viewHeroesByLevel(Integer.parseInt(object.toString()));
                case 3:
                    return heroManager.viewHeroesByName((String) object);
                case 4:
                    return campaignManager.findHeroesByMission((Mission)object);
                default:
                    return null;
            }
        }

        @Override
        protected void done() {
            try {
                heroList = get();
                fireTableDataChanged();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception: ", e);
                return;
            }
            log.info("Filtering heroes succeed");
        }
    }

    private class AddSwingWorker extends SwingWorker<Void, Void> {

        private final HeroManager heroManager;
        private final Hero hero;

        public AddSwingWorker(HeroManager heroManager, Hero hero) {
            this.heroManager = heroManager;
            this.hero = hero;
        }

        @Override
        protected Void doInBackground() throws ValidationException {
            heroManager.createHero(hero);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                heroList.add(hero);
                int lastRow = heroList.size() - 1;
                fireTableRowsInserted(lastRow, lastRow);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Adding hero failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Hero added successfully.");
        }
    }

    private class UpdateSwingWorker extends SwingWorker <Void, Void> {

        private final HeroManager heroManager;
        private final Hero hero;
        private final int row;

        public UpdateSwingWorker(HeroManager heroManager, Hero hero, int row) {
            this.heroManager = heroManager;
            this.hero = hero;
            this.row = row;
        }


        @Override
        protected Void doInBackground() throws Exception {
            heroManager.updateHero(hero);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
                refreshTable();
            } catch (InterruptedException | ExecutionException e) {
                log.error("Updating hero failed: " + e.getCause().getMessage());
                JOptionPane.showMessageDialog(dialog, e.getCause().getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            log.info("Hero updated successfully.");
        }
    }

    private class DeleteSwingWorker extends SwingWorker <Void, Void> {

        private final HeroManager heroManager;
        private final int row;

        public DeleteSwingWorker(HeroManager heroManager, int rowIndex) {
            this.heroManager = heroManager;
            this.row = rowIndex;
        }

        @Override
        protected Void doInBackground() throws Exception {
            heroManager.deleteHero(heroManager.findHeroById((Long) getValueAt(row, 0)));
            return null;
        }

        @Override
        protected void done() {
            heroList.remove(row);
            fireTableRowsDeleted(row, row);
            log.info("Hero deleted successfully.");
        }
    }

    public List<Hero> getList() {
        return heroList;
    }

    public void setList(List<Hero> list) {
        heroList = list;
    }

    private AddSwingWorker addWorker;
    private UpdateSwingWorker updateWorker;
    private DeleteSwingWorker deleteWorker;
    private FilterSwingWorker filterWorker;



    public void addRow(Hero hero) {
        addWorker = new AddSwingWorker(heroManager, hero);
        addWorker.execute();
    }

    public void removeRow(int row) {
        deleteWorker = new DeleteSwingWorker(heroManager, row);
        deleteWorker.execute();
    }


    public void refreshTable() {
        readWorker = new ReadAllSwingWorker(heroManager);
        readWorker.execute();
    }

    public void filterTable(Object object, int filterType) {
        filterWorker = new FilterSwingWorker(heroManager, campaignManager, object, filterType);
        filterWorker.execute();
    }

    public void updateRow(Hero hero, int row) throws ValidationException {
        updateWorker = new UpdateSwingWorker(heroManager, hero, row);
        updateWorker.execute();
    }

}
