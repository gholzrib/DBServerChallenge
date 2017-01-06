package gholzrib.dbserverchallenge.core.models;

/**
 * Created by Gunther Ribak on 01/01/2017.
 * For more information contact me
 * through guntherhr@gmail.com
 */

public class Restaurant {

    private String id;
    private String name;
    private String vicinity;
    private int votes = 0;

    private ItemGeometry geometry;

    public class ItemGeometry {

        private ItemCoordinates location;

        public class ItemCoordinates {

            Double lat;
            Double lng;

            public Double getLat() {
                return lat;
            }

            public void setLat(Double lat) {
                this.lat = lat;
            }

            public Double getLng() {
                return lng;
            }

            public void setLng(Double lng) {
                this.lng = lng;
            }
        }

        public ItemCoordinates getLocation() {
            return location;
        }

        public void setLocation(ItemCoordinates location) {
            this.location = location;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public ItemGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(ItemGeometry geometry) {
        this.geometry = geometry;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }
}
