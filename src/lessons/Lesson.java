package lessons;

import game.TileMap;
import utilities.FileReader;
import utilities.GameAudio;
import utilities.TextBox;
import utilities.TileMapLoader;

import static game.Game.GAME;

/**
 * Manages game flow when in any of the Lesson states.
 * Has shared methods and fields across all lesson types.
 */
public abstract class Lesson {
    private static int oldTime;

    private int grade;
    private int rounds;
    private int id;
    private double score;
    private boolean feedback;
    private boolean rules;
    private boolean finished;
    private String questionText;
    private String feedbackText;

    Lesson(int grade) {
        this.grade = grade;
    }

    int getGrade() { return grade; }
    void setGrade(int grade) { this.grade = grade; }

    int getRounds() { return rounds; }
    void setRounds(int rounds) { this.rounds = rounds; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    double getScore() { return score; }
    void setScore(double score) { this.score = score; }

    public boolean isFeedback() { return feedback; }
    public void setFeedback(boolean feedback) { this.feedback = feedback; }

    public boolean isRules() { return rules; }
    public void setRules(boolean rules) { this.rules = rules; }

    public boolean isNotFinished() { return !finished; }

    void setFinished() { this.finished = true; }

    void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getFeedbackText() { return feedbackText; }
    void setFeedbackText(String feedbackText) { this.feedbackText = feedbackText; }

    /**
     * Starts a Lesson game state.
     * Sets the player's position and displays a TextBox and Menu.
     *
     * @param id the identifier for the lesson subject (0 = DT, 1 = food tech, 2 = PE, 3 = chemistry, 4 = ICT)
     */
    public static void startLesson(int id) {
        TileMap currentMap = TileMapLoader.tileMaps.get(GAME.getMapId());
        currentMap.resetNPCs();
        int grade = (GAME.getGradeValue(id) / 10) + 1;
        if (grade > 4) { grade = 4; }
        oldTime = GAME.getTime();
        GAME.setTime(id + 3);
        GameAudio.startMusic(GameAudio.music_lesson);

        switch (id) {
            case 0:
            case 1:
                GAME.setLesson(new LessonTypeB(id + 1, grade));
                break;
            case 2:
                GAME.setLesson(new LessonTypeC(grade));
                break;
            case 3:
            case 4:
                GAME.setLesson(new LessonTypeA(id - 3, grade));
                break;
        }
        GAME.getLesson().id = id;

    }

    /**
     * Ends the Lesson state.
     * Increments the respective grade score and game points.
     * Closes the Lesson shell screens, resets the player's condition and increments the game time.
     */
    public void finish() {
        int increase = (int)((score / rounds) * 5);
        GAME.increaseGradeValue(id, increase);
        GAME.increasePoints(1, id, increase);
        GAME.setTime(oldTime);

        GAME.setTime(GAME.getTime() + 1);
        GAME.setTextBox(null);
        GAME.setMenu(null);
        GAME.setLesson(null);
        GAME.getPlayer().setCondition(0);
        GAME.doTransition();
        GameAudio.startMusic(GameAudio.music_school);
    }

    /**
     * Sets the starting concentration/energy in Chemistry/ICT/PE.
     *
     * @param grade the player's grade in the respective lesson
     * @return the starting concentration/energy
     */
    static int setStart(int grade) {
        switch (grade) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
        }
        return 0;
    }

    /**
     * Sets the number of questions in Chemistry/ICT lessons
     *
     * @param grade the player's respective grade in the lesson
     * @return the number of questions
     */
    static int setNumberOfQuestions(int grade) {
        switch (grade) {
            case 1:
                return 5;
            case 2:
                return 7;
            case 3:
                return 9;
            case 4:
                return 12;
        }
        return 5;
    }

    /**
     * Generates a question/task in Chemistry/ICT or DT/Food Tech lessons
     *
     * @param lessonId the identifier for the type of lesson (0 = chemistry, 1 = ICT, else DT/food tech)
     *
     * @param lv1 the chance the question type is level 1
     * @param lv2 the chance the question type is level 2
     * @return the type of question's id
     */
    int generateQuestion(int lessonId, double lv1, double lv2) {
        double r = Math.random();
        if (r < lv1) {
            if (lessonId == 0 || lessonId == 1) { questionText = FileReader.getLessonString(0); }
            else { questionText = FileReader.getLessonString(18); }
            return 0;
        }
        else if (r < lv2) {
            if (lessonId == 0) { questionText = FileReader.getLessonString(1); }
            else if (lessonId == 1) { questionText = FileReader.getLessonString(2); }
            else { questionText = FileReader.getLessonString(19); }
            return 1;
        }
        else {
            if (lessonId == 0) { questionText = FileReader.getLessonString(3); }
            else if (lessonId == 1) { questionText = FileReader.getLessonString(4); }
            else { questionText = FileReader.getLessonString(20); }
            return 2;
        }
    }

    /**
     * Shows the rules for the respective lesson type
     *
     * @return the identifier for the lesson subject (0 = DT, 1 = food tech, 2 = PE, 3 = chemistry, 4 = ICT)
     */
    public TextBox showRules() {
        switch (id) {
            case 0:
            case 1:
                return new TextBox(6, FileReader.getInteractiveString(1));
            case 2:
                return new TextBox(7, FileReader.getInteractiveString(2));
            case 3:
            case 4:
                return new TextBox(1, FileReader.getInteractiveString(0));
            default:
                return null;
        }
    }

    /**
     * Checks if the player has answered a question (or equivalent)
     */
    public void checkFinish() {
        if (finished) { finish(); }
        else {
            GAME.getMenu().setVisible(true);
            GAME.setTextBox( new TextBox(0, questionText));
        }
    }

    public abstract void doAction(int action);
}
