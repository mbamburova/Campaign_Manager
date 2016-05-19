package campaignmanager;

import java.util.List;

/**
 * Created by Anonym on 8. 3. 2016.
 */
public interface MissionManager {

    /**
     *
     * @param mission
     */
    void createMission(Mission mission);

    /**
     *
     * @param mission
     */
    void updateMission(Mission mission);

    /**
     *
     * @param id
     * @return
     */
    Mission findMissionById(Long id);

    /**
     *
     * @return
     */
    List<Mission> findAllMission();

    /**
     *
     * @return
     */
    List<Mission> viewAvailableMissions();

    /**
     *
     */
    List<Mission> viewMissionsForLevel(int level);
}