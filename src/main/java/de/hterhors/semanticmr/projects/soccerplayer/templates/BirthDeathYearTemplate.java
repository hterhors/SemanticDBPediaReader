package de.hterhors.semanticmr.projects.soccerplayer.templates;

import java.util.ArrayList;
import java.util.List;

import de.hterhors.semanticmr.candprov.sf.AnnotationCandidateRetrievalCollection;
import de.hterhors.semanticmr.candprov.sf.ISlotTypeAnnotationCandidateProvider;
import de.hterhors.semanticmr.crf.model.AbstractFactorScope;
import de.hterhors.semanticmr.crf.model.Factor;
import de.hterhors.semanticmr.crf.structure.annotations.AbstractAnnotation;
import de.hterhors.semanticmr.crf.structure.annotations.EntityTemplate;
import de.hterhors.semanticmr.crf.structure.annotations.SingleFillerSlot;
import de.hterhors.semanticmr.crf.structure.annotations.SlotType;
import de.hterhors.semanticmr.crf.templates.AbstractFeatureTemplate;
import de.hterhors.semanticmr.crf.variables.Instance;
import de.hterhors.semanticmr.crf.variables.State;
import de.hterhors.semanticmr.projects.soccerplayer.templates.BirthDeathYearTemplate.BirthDeathYearScope;

/**
 * @author hterhors
 *
 * @date Nov 15, 2017
 */
public class BirthDeathYearTemplate extends AbstractFeatureTemplate<BirthDeathYearScope> {

	AnnotationCandidateRetrievalCollection candidateRetrieval;

	public BirthDeathYearTemplate(AnnotationCandidateRetrievalCollection candidateRetrieval) {

		this.candidateRetrieval = candidateRetrieval;
	}

	private static enum EYearType {

		BIRTH, DEATH;

	}

	static class BirthDeathYearScope extends AbstractFactorScope {

		/**
		 * The currently assigned year.
		 */
		final int assignedYear;

		/**
		 * The document that contains other annotations of years.
		 */
		final Instance instance;

		/**
		 * BirthYear or DeathYear
		 */
		final EYearType context;

		final SlotType slot;

		public BirthDeathYearScope(AbstractFeatureTemplate<BirthDeathYearScope> template, Instance instance,
				final int assignedYear, EYearType context, SlotType slot) {
			super(template);
			this.instance = instance;
			this.assignedYear = assignedYear;
			this.context = context;
			this.slot = slot;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + assignedYear;
			result = prime * result + ((context == null) ? 0 : context.hashCode());
			result = prime * result + ((instance == null) ? 0 : instance.hashCode());
			result = prime * result + ((slot == null) ? 0 : slot.hashCode());
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
			BirthDeathYearScope other = (BirthDeathYearScope) obj;
			if (assignedYear != other.assignedYear)
				return false;
			if (context != other.context)
				return false;
			if (instance == null) {
				if (other.instance != null)
					return false;
			} else if (!instance.equals(other.instance))
				return false;
			if (slot == null) {
				if (other.slot != null)
					return false;
			} else if (!slot.equals(other.slot))
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
	public List<BirthDeathYearScope> generateFactorScopes(State state) {
		List<BirthDeathYearScope> factors = new ArrayList<>();

		for (EntityTemplate annotation : super.<EntityTemplate>getPredictedAnnotations(state)) {

			SingleFillerSlot birthYear = annotation.getSingleFillerSlot(SlotType.get("birthYear"));

			if (birthYear.containsSlotFiller()) {
				final int birthYearSF = birthYear.getSlotFiller().asInstanceOfDocumentLinkedAnnotation()
						.getSurfaceFormAsInt();
				factors.add(new BirthDeathYearScope(this, state.getInstance(), birthYearSF, EYearType.BIRTH,
						birthYear.slotType));
			}

			SingleFillerSlot deathYear = annotation.getSingleFillerSlot(SlotType.get("deathYear"));

			if (deathYear.containsSlotFiller()) {

				final int deathYearSF = deathYear.getSlotFiller().asInstanceOfDocumentLinkedAnnotation()
						.getSurfaceFormAsInt();
				factors.add(new BirthDeathYearScope(this, state.getInstance(), deathYearSF, EYearType.DEATH,
						deathYear.slotType));
			}

		}
		return factors;
	}

	@Override
	public void generateFeatureVector(Factor<BirthDeathYearScope> factor) {
		if (factor.getFactorScope().context == EYearType.BIRTH)
			addBirthYearFactor(factor);
		else if (factor.getFactorScope().context == EYearType.DEATH)
			addDeathYearFactor(factor);
	}

	private void addBirthYearFactor(Factor<BirthDeathYearScope> factor) {

		final List<AbstractAnnotation> possibleBirthYearAnnotations = new ArrayList<>();

		for (ISlotTypeAnnotationCandidateProvider provider : candidateRetrieval
				.getSlotTypeCandidateProvider(factor.getFactorScope().instance)) {
			possibleBirthYearAnnotations.addAll(provider.getCandidates(factor.getFactorScope().slot));
		}

		final int assignedYear = factor.getFactorScope().assignedYear;

		boolean isEarliestMentionedYear = true;

		for (AbstractAnnotation namedEntityLinkingAnnotation : possibleBirthYearAnnotations) {

			/*
			 * Note, that we do not have to skip the comparison with itself as we check
			 * less-or-equal.
			 */
			final int birthYearCandidate = namedEntityLinkingAnnotation.asInstanceOfDocumentLinkedAnnotation()
					.getSurfaceFormAsInt();

			isEarliestMentionedYear &= assignedYear <= birthYearCandidate;

			/*
			 * To speed up the process we can stop here if there is a year which is earlier.
			 */
			if (!isEarliestMentionedYear)
				break;
		}

		factor.getFeatureVector().set("Assigned birth year is earliest mentioned year in text",
				isEarliestMentionedYear);
	}

	private void addDeathYearFactor(Factor<BirthDeathYearScope> factor) {

		final List<AbstractAnnotation> possibleBirthYearAnnotations = new ArrayList<>();

		for (ISlotTypeAnnotationCandidateProvider provider : candidateRetrieval
				.getSlotTypeCandidateProvider(factor.getFactorScope().instance)) {
			possibleBirthYearAnnotations.addAll(provider.getCandidates(factor.getFactorScope().slot));
		}
		final int assignedYear = factor.getFactorScope().assignedYear;

		boolean isLatestMentionedYear = true;

		for (AbstractAnnotation namedEntityLinkingAnnotation : possibleBirthYearAnnotations) {

			/*
			 * Note, that we do not have to skip the comparison with itself as we check
			 * less-or-equal.
			 */
			final int deathYearCandidate = namedEntityLinkingAnnotation.asInstanceOfDocumentLinkedAnnotation()
					.getSurfaceFormAsInt();

			isLatestMentionedYear &= assignedYear >= deathYearCandidate;

			/*
			 * To speed up the process we can stop here if there is a year which is earlier.
			 */
			if (!isLatestMentionedYear)
				break;
		}

		factor.getFeatureVector().set("Assigned death year is latest mentioned year in text", isLatestMentionedYear);
	}
}
