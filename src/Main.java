import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Iterator;
import java.io.*;

//cd /Users/penguin/Documents/rust/kpp_ddpu/java/A1_4-2/out/production/A1_4-2 && java Main -auto



 class Main {
    private static final String FILE_NAME = "data.ser";

    public static void main(String[] args) {
        boolean isAutoMode = false;

        if (args.length > 0 && args[0].equals("-auto")) {
            isAutoMode = true;
        }

        GenericContainer<Agency.Vacancy> generic;

        if (isAutoMode) {
            generic = generateAutoData();
        } else {
                generic = loadDataFromFile(FILE_NAME);

            if (generic == null) {
                generic = new GenericContainer<>();
                System.out.println("New container created.");
            } else {
                System.out.println("Container loaded from file.");
            }
        }

        if (!isAutoMode) {
            processInput(generic);

            saveDataToFile(generic, FILE_NAME);
        } else {
            manipulateAutoData(generic);
        }
    }

    private static void manipulateAutoData(GenericContainer<Agency.Vacancy> data) {
        System.out.println("Manipulating auto-generated data...");

        System.out.println("Adding data:");
        data.pushBack(createVacancy("NewCompany", "NewSpecialization", "NewConditions", "75000",
                "NewWorkerSpec", "5", "university"));
        printVacancies(data);

        System.out.println("Removing data at index 2:");
        Agency.Vacancy removedVacancy = data.deleteByNumber(2);
        if (removedVacancy != null) {
            System.out.println("Removed vacancy: " + removedVacancy);
        } else {
            System.out.println("Error removing vacancy.");
        }
        printVacancies(data);

        System.out.println("Listing all data:");
        printVacancies(data);
    }

    private static GenericContainer<Agency.Vacancy> generateAutoData() {
        GenericContainer<Agency.Vacancy> generatedData = new GenericContainer<>();

        for (int i = 0; i < 5; i++) {
            String companyName = "Company" + (i + 1);
            String specialization = "Specialization" + (i + 1);
            String conditions = "Conditions" + (i + 1);
            String salary = String.valueOf(50000 + i * 10000);
            String workerSpecializationName = "WorkerSpec" + (i + 1);
            String workExpYearsString = String.valueOf(2 + i);
            String educationString = (i % 2 == 0) ? "university" : "school";

            Agency.Vacancy newVacancy = createVacancy(companyName, specialization, conditions, salary,
                    workerSpecializationName, workExpYearsString, educationString);

            if (newVacancy != null) {
                generatedData.pushBack(newVacancy);
            }
        }

        System.out.println("Generated 5 data strings.");

        return generatedData;
    }

    private static GenericContainer<Agency.Vacancy> loadDataFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (GenericContainer<Agency.Vacancy>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error reading data from file: " + e.getMessage());

            return null;
        }
    }

    private static void saveDataToFile(GenericContainer<Agency.Vacancy> data, String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.out.println("Error saving data to file: " + e.getMessage());
        }
    }

    private static Agency.Vacancy createVacancy(String companyName, String specialization, String conditions,
                                                String salary, String workerSpecializationName,
                                                String workExpYearsString, String educationString) {
        try {
            return new Agency.Vacancy(companyName, specialization, conditions, salary,
                    workerSpecializationName, workExpYearsString, educationString);
        } catch (Agency.VacancyCreationException e) {
            System.out.println("Error creating vacancy: " + e.getMessage());
            return null;
        }
    }

    private static void printVacancies(GenericContainer<Agency.Vacancy> data) {
        int id = 1;
        for (Agency.Vacancy vacancy : data) {
            System.out.println("(" + id++ + ") " + vacancy);
        }

    }

    private static void processInput(GenericContainer<Agency.Vacancy> data) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Input option:\n1) List all vacancies\n2) Add vacancy\n3) Remove vacancy by id\n4) Exit");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    printVacancies(data);
                    break;
                case "2":
                    addVacancy(data);
                    break;
                case "3":
                    removeVacancyById(data);
                    break;
                case "4":
                    return;
                default:
                    break;
            }
        }
    }




    private static void removeVacancyById(GenericContainer<Agency.Vacancy> data) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter index of vacancy to remove:");
        int vacancyIndex;
        try {
            vacancyIndex = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid index.");
            return;
        }

        if (vacancyIndex < 0 || vacancyIndex >= data.size()) {
            System.out.println("Invalid index. Please enter a valid index.");
            return;
        }

        Agency.Vacancy removedVacancy = data.deleteByNumber(vacancyIndex);
        if (removedVacancy != null) {
            System.out.println("Vacancy removed: " + removedVacancy.toString());
        } else {
            System.out.println("Error removing vacancy.");
        }
    }

    private static void addVacancy(GenericContainer<Agency.Vacancy> data) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Company name: ");
        String companyName = scanner.nextLine();

        System.out.println("Company specialization: ");
        String specialization = scanner.nextLine();

        System.out.println("Company conditions: ");
        String conditions = scanner.nextLine();

        System.out.println("Salary: ");
        String salaryString = scanner.nextLine();

        System.out.println("Worked specialization name (or empty): ");
        String workerSpecializationName = scanner.nextLine();

        String workExpYearsString = "0";
        if (!workerSpecializationName.trim().isEmpty()) {
            System.out.println("Worked experience years: ");
            workExpYearsString = scanner.nextLine();
        }

        System.out.println("Worked Education (empty, school, university): ");
        String educationString = scanner.nextLine();

        Agency.Vacancy newVacancy = createVacancy(companyName, specialization, conditions, salaryString,
                workerSpecializationName, workExpYearsString, educationString);

        if (newVacancy != null) {
            data.pushBack(newVacancy);
            System.out.println("Vacancy added");
        }
    }

}

class Utils {
    public static GenericContainer<Agency.Vacancy> sortByCompanyName(GenericContainer<Agency.Vacancy> data) {
        return sortData(data, Comparator.comparing(vacancy -> vacancy.getCompanyName()));
    }

    public static GenericContainer<Agency.Vacancy> sortBySpecialization(GenericContainer<Agency.Vacancy> data) {
        return sortData(data, Comparator.comparing(vacancy -> vacancy.getSpecialization()));
    }

    public static GenericContainer<Agency.Vacancy> sortByEducation(GenericContainer<Agency.Vacancy> data) {
        return sortData(data, Comparator.comparing(vacancy -> vacancy.getEducation()));
    }

    private static GenericContainer<Agency.Vacancy> sortData(GenericContainer<Agency.Vacancy> data, Comparator<Agency.Vacancy> comparator) {
        List<Agency.Vacancy> sortedList = new ArrayList<>();
        data.forEach(sortedList::add);
        sortedList.sort(comparator);

        return new GenericContainer<>(sortedList);

    }
}

class Agency {

    public static class Vacancy implements Comparable<Vacancy>,Serializable {
        private final String companyName;
        private final String specialization;
        private final String conditions;
        private final int salary;
        private final WorkerRequirements workerRequirements;

        public Vacancy(String companyName, String specialization, String conditions,
                       String salaryString, String workerSpecializationName,
                       String workExpYearsString, String educationString)
                throws VacancyCreationException {
            this.companyName = companyName.trim();
            this.specialization = specialization.trim();
            this.conditions = conditions.trim();
            this.salary = parseSalary(salaryString);

            WorkerSpecialization workerSpecialization = parseWorkerSpecialization(workerSpecializationName, workExpYearsString);
            Education education = parseEducation(educationString);

            this.workerRequirements = new WorkerRequirements(workerSpecialization, education);
        }

        private int parseSalary(String salaryString) throws VacancyCreationException {
            try {
                return Integer.parseInt(salaryString.trim());
            } catch (NumberFormatException e) {
                throw new VacancyCreationException("Error parsing salary: " + salaryString);
            }
        }

        private WorkerSpecialization parseWorkerSpecialization(String workerSpecializationName, String workExpYearsString) throws VacancyCreationException {
            if (workerSpecializationName == null || workerSpecializationName.isEmpty()) {
                return null;
            }

            try {
                int workExpYears = Integer.parseInt(workExpYearsString.trim());
                return new WorkerSpecialization(workerSpecializationName.trim(), workExpYears);
            } catch (NumberFormatException e) {
                throw new VacancyCreationException("Error parsing work experience years: " + workExpYearsString);
            }
        }

        private Education parseEducation(String educationString) throws VacancyCreationException {
            switch (educationString.toLowerCase()) {
                case "":
                    return Education.NONE;
                case "school":
                    return Education.SCHOOL;
                case "university":
                    return Education.UNIVERSITY;
                default:
                    throw new VacancyCreationException("Error parsing education string: " + educationString);
            }
        }

        public String getCompanyName() {
            return companyName;
        }

        public String getSpecialization() {
            return specialization;
        }

        public Education getEducation() {
            return workerRequirements.education;
        }

        public String getConditions() {
            return conditions;
        }

        public int getSalary() {
            return salary;
        }

        public WorkerRequirements getWorkerRequirements() {
            return workerRequirements;
        }

        @Override
        public String toString() {
            return String.format("%s: %s(%s) - %s$ %s", companyName, specialization, conditions, salary, workerRequirements.getEducation());
        }

        @Override
        public int compareTo(Vacancy other) {
            return companyName.compareTo(other.companyName);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vacancy vacancy = (Vacancy) o;
            return salary == vacancy.salary &&
                    companyName.equals(vacancy.companyName) &&
                    specialization.equals(vacancy.specialization) &&
                    conditions.equals(vacancy.conditions) &&
                    workerRequirements.equals(vacancy.workerRequirements);
        }

        @Override
        public int hashCode() {
            return Objects.hash(companyName, specialization, conditions, salary, workerRequirements);
        }
    }

    public static class WorkerRequirements implements  Serializable {
        private final WorkerSpecialization specialization;
        private final Education education;

        public WorkerRequirements(WorkerSpecialization specialization, Education education) {
            this.specialization = specialization;
            this.education = education;
        }

        public WorkerSpecialization getSpecialization() {
            return specialization;
        }

        public Education getEducation() {
            return education;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkerRequirements that = (WorkerRequirements) o;
            return Objects.equals(specialization, that.specialization) &&
                    education == that.education;
        }

        @Override
        public int hashCode() {
            return Objects.hash(specialization, education);
        }
    }

    public static class WorkerSpecialization implements  Serializable{
        private final String specializationName;
        private final int workExpYears;

        public WorkerSpecialization(String specializationName, int workExpYears) {
            this.specializationName = specializationName;
            this.workExpYears = workExpYears;
        }

        public String getSpecializationName() {
            return specializationName;
        }

        public int getWorkExpYears() {
            return workExpYears;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WorkerSpecialization that = (WorkerSpecialization) o;
            return workExpYears == that.workExpYears &&
                    specializationName.equals(that.specializationName);
        }

    }

    public enum Education {
        NONE,
        SCHOOL,
        UNIVERSITY
    }

    public static class VacancyCreationException extends Exception {
        public VacancyCreationException(String message) {
            super(message);
        }
    }
}

class GenericContainer<T> implements Iterable<T>,Serializable {
    private LinkedList<T> container;

    public GenericContainer() {
        this.container = new LinkedList<>();
    }

    public GenericContainer(List<T> input) {
        this.container = new LinkedList<>(input);
    }

    public void pushBack(T item) {
        container.addLast(item);
    }

    public T deleteByNumber(int index) {
        if (index < 0 || index >= container.size()) {
            return null;
        }

        return container.remove(index);
    }

    public int size() {
        return container.size();
    }

    @Override
    public Iterator<T> iterator() {
        return container.iterator();
    }
}