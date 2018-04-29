package cas.se3xa3.bitsplease.model.solver;

import cas.se3xa3.bitsplease.model.Coordinate;

import java.util.Set;

/**
 * Represents a solver's suggestion on what tile to place.
 * Created on 12/11/2015.
 */
public class Hint {
    private Set<Coordinate> tilesInvolved;
    private String explanation;

    public Hint(Set<Coordinate> tilesInvolved, String explanation) {
        this.tilesInvolved = tilesInvolved;
        this.explanation = explanation;
    }

    public Set<Coordinate> getTilesInvolved() {
        return tilesInvolved;
    }

    public void setTilesInvolved(Set<Coordinate> tilesInvolved) {
        this.tilesInvolved = tilesInvolved;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
