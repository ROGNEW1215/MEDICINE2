package com.example.medicine.model;

public class Medication {

    private long id;
    private String medName;
    private String dosage;
    private String remindTime;
    private int iconType;
    private int alarmId;
    private int isTaken;

    public Medication() {
    }

    public Medication(long id, String medName, String dosage, String remindTime,
                      int iconType, int alarmId) {
        this.id = id;
        this.medName = medName;
        this.dosage = dosage;
        this.remindTime = remindTime;
        this.iconType = iconType;
        this.alarmId = alarmId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMedName() {
        return medName;
    }

    public void setMedName(String medName) {
        this.medName = medName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getRemindTime() {
        return remindTime;
    }

    public void setRemindTime(String remindTime) {
        this.remindTime = remindTime;
    }

    public int getIconType() {
        return iconType;
    }

    public void setIconType(int iconType) {
        this.iconType = iconType;
    }

    public int getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(int alarmId) {
        this.alarmId = alarmId;
    }

    public int getIsTaken() {
        return isTaken;
    }

    public void setIsTaken(int isTaken) {
        this.isTaken = isTaken;
    }

    public boolean isTaken() {
        return isTaken == 1;
    }
}
