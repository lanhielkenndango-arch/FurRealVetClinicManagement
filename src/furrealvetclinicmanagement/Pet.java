package furrealvetclinicmanagement;

public class Pet implements Identifiable {
    private int petId;
    private int clientId;
    private String petName;
    private String petType;
    private String breed;
    private int age;

    public Pet(int petId, int clientId, String petName, String petType, String breed, int age) {
        this.petId = petId;
        this.clientId = clientId;
        this.petName = petName;
        this.petType = petType;
        this.breed = breed;
        this.age = age;
    }

    public Pet(int clientId, String petName, String petType, String breed, int age) {
        this.clientId = clientId;
        this.petName = petName;
        this.petType = petType;
        this.breed = breed;
        this.age = age;
    }

    @Override
    public int getId() {
        return petId;
    }

    public int getPetId() {
        return petId;
    }

    public int getClientId() {
        return clientId;
    }

    public String getPetName() {
        return petName;
    }

    public String getPetType() {
        return petType;
    }

    public String getBreed() {
        return breed;
    }

    public int getAge() {
        return age;
    }
}
