package dam.pmdm.api_quiz.model;

public class Question implements java.io.Serializable {
    private int idquestion;
    private int idunit;
    private String title;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private int correct;

    public Question() {
    }
    public Question(int idquestion, int idunit, String title, String answer1, String answer2, String answer3, String answer4, int correct) {
        this.idquestion = idquestion;
        this.idunit = idunit;
        this.title = title;
        this.answer1 = answer1;
        this.answer2 = answer2;
        this.answer3 = answer3;
        this.answer4 = answer4;
        this.correct = correct;
    }

    public int getIdquestion() {
        return idquestion;
    }

    public void setIdquestion(int idquestion) {
        this.idquestion = idquestion;
    }

    public int getIdunit() {
        return idunit;
    }

    public void setIdunit(int idunit) {
        this.idunit = idunit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAnswer1() {
        return answer1;
    }

    public void setAnswer1(String answer1) {
        this.answer1 = answer1;
    }

    public String getAnswer2() {
        return answer2;
    }

    public void setAnswer2(String answer2) {
        this.answer2 = answer2;
    }

    public String getAnswer3() {
        return answer3;
    }

    public void setAnswer3(String answer3) {
        this.answer3 = answer3;
    }

    public String getAnswer4() {
        return answer4;
    }

    public void setAnswer4(String answer4) {
        this.answer4 = answer4;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    /** Clase para añadir pregunta (Sin idquestion) */
    public static class QuestionAddRequest {
        private int idunit;
        private String title;
        private String answer1, answer2, answer3, answer4;
        private int correct;

        public QuestionAddRequest(int idunit, String title, String a1, String a2, String a3, String a4, int correct) {
            this.idunit = idunit;
            this.title = title;
            this.answer1 = a1;
            this.answer2 = a2;
            this.answer3 = a3;
            this.answer4 = a4;
            this.correct = correct;
        }
    }

    /** Clase para editar pregunta (Con idquestion e idunit) */
    public static class QuestionUpdateRequest {
        private int idquestion;
        private int idunit;
        private String title;
        private String answer1, answer2, answer3, answer4;
        private int correct;

        public QuestionUpdateRequest(int idq, int idu, String t, String a1, String a2, String a3, String a4, int c) {
            this.idquestion = idq;
            this.idunit = idu;
            this.title = t;
            this.answer1 = a1;
            this.answer2 = a2;
            this.answer3 = a3;
            this.answer4 = a4;
            this.correct = c;
        }
    }

}