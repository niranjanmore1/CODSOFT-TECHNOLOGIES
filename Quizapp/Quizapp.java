import java.io.*;
import java.util.*;

class Question {
    String questionText;
    String[] options;
    int correctOption;
    String category;

    public Question(String questionText, String[] options, int correctOption, String category) {
        this.questionText = questionText;
        this.options = options;
        this.correctOption = correctOption;
        this.category = category;
    }
}

class UserProfile {
    String username;
    int totalQuizzesPlayed;
    int highestScore;
    double averageAccuracy;
    Set<String> achievements;

    public UserProfile(String username) {
        this.username = username;
        this.totalQuizzesPlayed = 0;
        this.highestScore = 0;
        this.averageAccuracy = 0.0;
        this.achievements = new HashSet<>();
    }

    public void updateProfile(int score, double accuracy) {
        totalQuizzesPlayed++;
        if (score > highestScore) {
            highestScore = score;
        }
        averageAccuracy = ((averageAccuracy * (totalQuizzesPlayed - 1)) + accuracy) / totalQuizzesPlayed;
        if (score >= 5) achievements.add("Quiz Master");
        if (accuracy == 100.0) achievements.add("Perfect Accuracy");
    }

    @Override
    public String toString() {
        return "Username: " + username + "\n" +
                "Total Quizzes Played: " + totalQuizzesPlayed + "\n" +
                "Highest Score: " + highestScore + "\n" +
                "Average Accuracy: " + String.format("%.2f", averageAccuracy) + "%\n" +
                "Achievements: " + achievements.toString();
    }
}

public class Quizapp
{
    private static List<Question> questionBank = new ArrayList<>();
    private static Map<String, UserProfile> userProfiles = new HashMap<>();
    private static UserProfile currentUser;

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        loadQuestions();
        loadUserProfiles();

        System.out.println("------------------------------------------------------------");
        System.out.println("            Welcome to the Ultimate Quiz Application!");
        System.out.println("      Test your knowledge across various categories and win!");
        System.out.println("------------------------------------------------------------");

        currentUser = loginUser(scanner);

        boolean playing = true;
        while (playing) {
            System.out.println("\n------------------------------------------------------------");
            System.out.println("Choose a category:");
            System.out.println("1. Science\n2. History\n3. Math\n4. Exit");
            System.out.println("------------------------------------------------------------");
            int categoryChoice = getValidInput(scanner, 1, 4);

            if (categoryChoice == 4) break;

            playQuiz(scanner, categoryChoice);

            System.out.println("\n------------------------------------------------------------");
            System.out.println("Your Profile:");
            System.out.println(currentUser);
            System.out.println("------------------------------------------------------------");

            System.out.println("Do you want to play again? (yes/no)");
            String playAgain = scanner.nextLine().trim();
            if (!playAgain.equalsIgnoreCase("yes")) playing = false;
        }

        saveUserProfiles();
        System.out.println("\n------------------------------------------------------------");
        System.out.println("         Thank you for playing! See you next time!");
        System.out.println("------------------------------------------------------------");
    }

    private static void playQuiz(Scanner scanner, int categoryChoice) {
        String category = switch (categoryChoice) {
            case 1 -> "Science";
            case 2 -> "History";
            case 3 -> "Math";
            default -> "General";
        };

        System.out.println("\n------------------------------------------------------------");
        System.out.println("Starting quiz in category: " + category);
        System.out.println("------------------------------------------------------------");

        int score = 0;
        int totalQuestions = 0;
        int correctAnswers = 0;

        for (Question question : questionBank) {
            if (!question.category.equals(category)) continue;

            System.out.println("\n" + question.questionText);
            for (int i = 0; i < question.options.length; i++) {
                System.out.println((i + 1) + ". " + question.options[i]);
            }

            System.out.println("------------------------------------------------------------");
            System.out.println("Enter your answer (1-4):");
            System.out.println("------------------------------------------------------------");

            int answer = getValidInput(scanner, 1, 4);

            totalQuestions++;
            if (answer == question.correctOption) {
                System.out.println("Correct!");
                score++;
                correctAnswers++;
            } else {
                System.out.println("Wrong! The correct answer was: " + question.correctOption);
            }
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.println("Quiz Over!");
        System.out.println("Your Score: " + score);
        double accuracy = totalQuestions > 0 ? (correctAnswers * 100.0 / totalQuestions) : 0.0;
        System.out.println("Accuracy: " + String.format("%.2f", accuracy) + "%");
        System.out.println("------------------------------------------------------------");

        currentUser.updateProfile(score, accuracy);
    }

    private static void loadQuestions() {
        questionBank.add(new Question("What is the capital of France?", new String[]{"Paris", "London", "Berlin", "Madrid"}, 1, "General"));
        questionBank.add(new Question("Who developed the theory of relativity?", new String[]{"Newton", "Einstein", "Galileo", "Tesla"}, 2, "Science"));
        questionBank.add(new Question("What is 5 + 7?", new String[]{"10", "11", "12", "13"}, 3, "Math"));
        questionBank.add(new Question("Who was the first president of the USA?", new String[]{"Lincoln", "Washington", "Jefferson", "Adams"}, 2, "History"));
    }

    private static void loadUserProfiles() {
        File file = new File("userProfiles.txt");
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                UserProfile profile = new UserProfile(parts[0]);
                profile.totalQuizzesPlayed = Integer.parseInt(parts[1]);
                profile.highestScore = Integer.parseInt(parts[2]);
                profile.averageAccuracy = Double.parseDouble(parts[3]);
                userProfiles.put(parts[0], profile);
            }
        } catch (IOException e) {
            System.err.println("Error loading user profiles: " + e.getMessage());
        }
    }

    private static void saveUserProfiles() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("userProfiles.txt"))) {
            for (UserProfile profile : userProfiles.values()) {
                bw.write(profile.username + "," + profile.totalQuizzesPlayed + "," + profile.highestScore + "," + profile.averageAccuracy + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error saving user profiles: " + e.getMessage());
        }
    }

    private static UserProfile loginUser(Scanner scanner) {
        System.out.println("\n------------------------------------------------------------");
        System.out.println("Enter your username:");
        System.out.println("------------------------------------------------------------");
        String username = scanner.nextLine().trim();
        if (!userProfiles.containsKey(username)) {
            UserProfile newUser = new UserProfile(username);
            userProfiles.put(username, newUser);
            System.out.println("New user created.");
            return newUser;
        } else {
            System.out.println("Welcome back, " + username + "!");
            return userProfiles.get(username);
        }
    }

    private static int getValidInput(Scanner scanner, int min, int max) {
        while (true) {
            if (scanner.hasNextInt()) {
                int input = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (input >= min && input <= max) {
                    return input;
                }
            } else {
                scanner.nextLine(); // Clear invalid input
            }
            System.out.println("Invalid input. Please try again:");
        }
    }
}
