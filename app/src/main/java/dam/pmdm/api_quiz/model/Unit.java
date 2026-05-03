package dam.pmdm.api_quiz.model;

public class Unit {
    private int idunit;
    private int idmodule;
    private String name;

    private int totquestions;

    public Unit() {
    }
    public Unit(int idunit, int idmodule, String name, int totquestions) {
        this.idunit = idunit;
        this.idmodule = idmodule;
        this.name = name;
        this.totquestions = totquestions;
    }

    public int getIdunit() {
        return idunit;
    }

    public void setIdunit(int idunit) {
        this.idunit = idunit;
    }

    public int getIdmodule() {
        return idmodule;
    }

    public void setIdmodule(int idmodule) {
        this.idmodule = idmodule;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTotquestions() {
        return totquestions;
    }

    public void setTotquestions(int totquestions) {
        this.totquestions = totquestions;
    }

    public static class UnitUpdateRequest {
        private int idmodule;
        private String name;
        private int idunit;

        public UnitUpdateRequest(int idmodule, String name, int idunit) {
            this.idmodule = idmodule;
            this.name = name;
            this.idunit = idunit;
        }
    }

    public static class UnitAddRequest {
        private int idmodule;
        private String name;

        public UnitAddRequest(int idmodule, String name) {
            this.idmodule = idmodule;
            this.name = name;
        }
    }
}