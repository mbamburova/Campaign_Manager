package campaignmanager;

/**
 * Created by Anonym on 8. 3. 2016.
 */
public class Hero {

    private Long id;
    private String hero_name;
    private int hero_level;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return hero_name;
    }

    public void setName(String hero_name) {
        this.hero_name = hero_name;
    }

    public int getLevel() {
        return hero_level;
    }

    public void setLevel(int hero_level) {
        this.hero_level = hero_level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Hero hero = (Hero) o;

        return id.equals(hero.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Hero{" +
                "id=" + id +
                ", hero_name='" + hero_name + '\'' +
                ", hero_level=" + hero_level +
                '}';
    }
}
