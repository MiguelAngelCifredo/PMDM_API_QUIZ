package dam.pmdm.api_quiz.model;

public class Module {

    private int idmodule;
    private String name;
    private int totunits;

    public Module() {
    }

    public Module(int idmodule, String name, int totunits) {
        this.idmodule = idmodule;
        this.name = name;
        this.totunits = totunits;
    }

    public int getTotunits() {
        return totunits;
    }

    public void getTotunits(int total) {
        this.totunits = total;
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

    public static class AddResponse {
        private int idmodule;
        public int getIdmodule() { return idmodule; }
    }

}