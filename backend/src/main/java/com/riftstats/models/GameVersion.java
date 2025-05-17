package com.riftstats.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "game_versions")
public class GameVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "patch", nullable = false, unique = true)
    private String patch;

    public GameVersion() {
        this.id = (long) -1;
        this.patch = "";
    }

    public GameVersion(String patch) {
        this.patch = patch;
    }

    @Override
    public String toString() {
        return String.format("patch=%s", patch);
    }
}
