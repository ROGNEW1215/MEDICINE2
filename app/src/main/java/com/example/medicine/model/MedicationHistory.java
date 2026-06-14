package com.example.medicine.model;

public class MedicationHistory {

    private long id;
    private long medId;
    private String medName;
    private String takenTime;

    public MedicationHistory() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMedId() {
        return medId;
    }

    public void setMedId(long medId) {
        this.medId = medId;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getTakenTime() {
        return takenTime;
    }

    public void setTakenTime(String takenTime) {
        this.takenTime = takenTime;
    }
}
