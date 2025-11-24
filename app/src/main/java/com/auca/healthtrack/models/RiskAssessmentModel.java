package com.auca.healthtrack.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import org.hl7.fhir.r4.model.RiskAssessment;
import org.hl7.fhir.r4.model.CodeableConcept;
import org.hl7.fhir.r4.model.Coding;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Entity(tableName = "risk_assessment")
public class RiskAssessmentModel implements Parcelable {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private String id;

    @ColumnInfo(name = "risk_score")
    private double riskScore;

    @ColumnInfo(name = "prediction")
    private String prediction;

    @ColumnInfo(name = "probability")
    private double probability;

    @ColumnInfo(name = "date")
    private long date;

    public RiskAssessmentModel() {
    }

    public RiskAssessmentModel(String id, double riskScore, String prediction, double probability, long date) {
        this.id = id;
        this.riskScore = riskScore;
        this.prediction = prediction;
        this.probability = probability;
        this.date = date;
    }

    protected RiskAssessmentModel(Parcel in) {
        id = in.readString();
        riskScore = in.readDouble();
        prediction = in.readString();
        probability = in.readDouble();
        date = in.readLong();
    }

    public static final Creator<RiskAssessmentModel> CREATOR = new Creator<RiskAssessmentModel>() {
        @Override
        public RiskAssessmentModel createFromParcel(Parcel in) {
            return new RiskAssessmentModel(in);
        }

        @Override
        public RiskAssessmentModel[] newArray(int size) {
            return new RiskAssessmentModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeDouble(riskScore);
        dest.writeString(prediction);
        dest.writeDouble(probability);
        dest.writeLong(date);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    public String getPrediction() {
        return prediction;
    }

    public void setPrediction(String prediction) {
        this.prediction = prediction;
    }

    public double getProbability() {
        return probability;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public RiskAssessment toFhir() {
        RiskAssessment assessment = new RiskAssessment();
        assessment.setId(this.id);

        // Prediction as code
        CodeableConcept predictionConcept = new CodeableConcept();
        predictionConcept.setText(this.prediction);
        RiskAssessment.RiskAssessmentPredictionComponent predictionComponent = assessment.addPrediction();
        predictionComponent.setOutcome(predictionConcept);

        // Probability
        predictionComponent.setProbability(new BigDecimal(this.probability));

        // Risk Score isn't a direct standard field in RiskAssessment resource in the
        // same way,
        // often mapped to probability or a specific code. We'll add it as an extension
        // or just rely on probability.
        // For this model, we'll just map probability.

        assessment.setOccurrence(new org.hl7.fhir.r4.model.DateTimeType(new Date(this.date)));

        return assessment;
    }

    public static RiskAssessmentModel fromFhir(RiskAssessment assessment) {
        RiskAssessmentModel model = new RiskAssessmentModel();
        model.setId(assessment.getIdElement().getIdPart());

        if (assessment.hasPrediction()) {
            RiskAssessment.RiskAssessmentPredictionComponent prediction = assessment.getPredictionFirstRep();
            if (prediction.hasOutcome()) {
                model.setPrediction(prediction.getOutcome().getText());
            }
            if (prediction.hasProbabilityDecimalType()) {
                model.setProbability(prediction.getProbabilityDecimalType().getValue().doubleValue());
                model.setRiskScore(model.getProbability() * 100); // Example logic
            }
        }

        if (assessment.hasOccurrenceDateTimeType()) {
            model.setDate(assessment.getOccurrenceDateTimeType().getValue().getTime());
        }

        return model;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        RiskAssessmentModel that = (RiskAssessmentModel) o;
        return Double.compare(that.riskScore, riskScore) == 0 &&
                Double.compare(that.probability, probability) == 0 &&
                date == that.date &&
                Objects.equals(id, that.id) &&
                Objects.equals(prediction, that.prediction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, riskScore, prediction, probability, date);
    }

    @Override
    public String toString() {
        return "RiskAssessmentModel{" +
                "id='" + id + '\'' +
                ", riskScore=" + riskScore +
                ", prediction='" + prediction + '\'' +
                ", probability=" + probability +
                ", date=" + date +
                '}';
    }
}
