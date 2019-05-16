package de.hterhors.semanticmr.projects.soccerplayer.templates;

import java.util.ArrayList;
import java.util.List;

import de.hterhors.semanticmr.crf.factor.AbstractFactorScope;
import de.hterhors.semanticmr.crf.factor.Factor;
import de.hterhors.semanticmr.crf.structure.annotations.EntityTemplate;
import de.hterhors.semanticmr.crf.structure.slots.SingleFillerSlot;
import de.hterhors.semanticmr.crf.structure.slots.SlotType;
import de.hterhors.semanticmr.crf.templates.AbstractFeatureTemplate;
import de.hterhors.semanticmr.crf.variables.State;
import de.hterhors.semanticmr.projects.soccerplayer.templates.BirthDeathYearPairTemplate.BirthDeathYearPairScope;

/**
 * @author hterhors
 *
 * @date Nov 15, 2017
 */
public class BirthDeathYearPairTemplate extends AbstractFeatureTemplate<BirthDeathYearPairScope> {

	static class BirthDeathYearPairScope extends AbstractFactorScope<BirthDeathYearPairScope> {

		/**
		 * The currently assigned birth year.
		 */
		final int assignedBirthYear;

		/**
		 * The currently assigned death year.
		 */
		final int assignedDeathYear;

		public BirthDeathYearPairScope(AbstractFeatureTemplate<BirthDeathYearPairScope> template,
				final int assignedBirthYear, final int assignedDeathYear) {
			super(template);
			this.assignedBirthYear = assignedBirthYear;
			this.assignedDeathYear = assignedDeathYear;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + assignedBirthYear;
			result = prime * result + assignedDeathYear;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			BirthDeathYearPairScope other = (BirthDeathYearPairScope) obj;
			if (assignedBirthYear != other.assignedBirthYear)
				return false;
			if (assignedDeathYear != other.assignedDeathYear)
				return false;
			return true;
		}

		@Override
		public int implementHashCode() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public boolean implementEquals(Object obj) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	@Override
	public List<BirthDeathYearPairScope> generateFactorScopes(State state) {
		List<BirthDeathYearPairScope> factors = new ArrayList<>();

		/*
		 * For all soccer player in the document create an individual scope.
		 *
		 * In the lecture corpus there is only one soccer player per document.
		 *
		 */
		for (EntityTemplate annotation : super.<EntityTemplate>getPredictedAnnotations(state)) {

			SingleFillerSlot deathYear = annotation.getSingleFillerSlot(SlotType.get("deathYear"));

			/*
			 * If the birth year was not yet set, we don't need to generate any features
			 * here.
			 */
			if (deathYear == null || !deathYear.containsSlotFiller())
				continue;

			SingleFillerSlot birthYear = annotation.getSingleFillerSlot(SlotType.get("birthYear"));

			/*
			 * If the birth year was not yet set, we don't need to generate any features
			 * here.
			 */
			if (birthYear == null || !birthYear.containsSlotFiller())
				continue;

			final BirthDeathYearPairScope birthScope = new BirthDeathYearPairScope(this,
					birthYear.getSlotFiller().asInstanceOfDocumentLinkedAnnotation().getSurfaceFormAsInt(),
					deathYear.getSlotFiller().asInstanceOfDocumentLinkedAnnotation().getSurfaceFormAsInt());

			factors.add(birthScope);

		}

		return factors;
	}

	@Override
	public void generateFeatureVector(Factor<BirthDeathYearPairScope> factor) {

		final int assignedBirthYear = factor.getFactorScope().assignedBirthYear;
		final int assignedDeathYear = factor.getFactorScope().assignedDeathYear;

		factor.getFeatureVector().set("Birth Equal Death", assignedBirthYear == assignedDeathYear);

		if (assignedBirthYear <= assignedDeathYear - 70)
			factor.getFeatureVector().set("Dist Birth and Death >= 70", true);
		else if (assignedBirthYear <= assignedDeathYear - 60)
			factor.getFeatureVector().set("Dist Birth and Death >= 60", true);
		else if (assignedBirthYear <= assignedDeathYear - 50)
			factor.getFeatureVector().set("Dist Birth and Death >= 50", true);
		else if (assignedBirthYear <= assignedDeathYear - 40)
			factor.getFeatureVector().set("Dist Birth and Death >= 40", true);
		else if (assignedBirthYear <= assignedDeathYear - 30)
			factor.getFeatureVector().set("Dist Birth and Death >= 30", true);
		else if (assignedBirthYear <= assignedDeathYear - 20)
			factor.getFeatureVector().set("Dist Birth and Death >= 20", true);
		else if (assignedBirthYear <= assignedDeathYear - 10)
			factor.getFeatureVector().set("Dist Birth and Death >= 10", true);
		else
			factor.getFeatureVector().set("Dist Birth and Death < 10", true);

	}

}
