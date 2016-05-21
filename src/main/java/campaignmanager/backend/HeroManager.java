package campaignmanager.backend;

import java.util.List;

/**
 * Created by Anonym on 8. 3. 2016.
 */
public interface HeroManager {

    /**
     *
     * @param hero
     */
    void createHero(Hero hero);

    /**
     *
     * @param hero
     */
    void updateHero(Hero hero);

    /**
     *
     * @param hero
     */
    void deleteHero(Hero hero);

    /**
     *
     * @param id
     * @return
     */
    Hero findHeroById(Long id);

    /**
     *
     * @return
     */
    List<Hero> findAllHeroes();

    /**
     *
     * @return
     */
    List<Hero> viewFreeHeroes();

    /**
     *
     * @param level
     * @return
     */
    List<Hero> viewHeroesByLevel(int level);

    /**
     *
     * @param name
     * @return
     */
    List<Hero> viewHeroesByName(String name);
}
