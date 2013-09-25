package de.prob.model.eventb.theory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eventb.core.ast.FormulaFactory;
import org.eventb.core.ast.LanguageVersion;
import org.eventb.core.ast.extension.IFormulaExtension;
import org.eventb.core.ast.extension.datatype.IArgument;
import org.eventb.core.ast.extension.datatype.IConstructorMediator;
import org.eventb.core.ast.extension.datatype.IDatatypeExtension;
import org.eventb.core.ast.extension.datatype.ITypeConstructorMediator;

import de.prob.animator.domainobjects.EventB;
import de.prob.model.representation.AbstractElement;
import de.prob.model.representation.ModelElementList;
import de.prob.unicode.UnicodeTranslator;

public class DataType extends AbstractElement {

	private final String identifierString;
	private final EventB identifier;
	private IDatatypeExtension typeDef = null;
	private final List<Type> typeArguments = new ModelElementList<Type>();
	private final List<DataTypeConstructor> dataTypeConstructors = new ModelElementList<DataTypeConstructor>();

	public DataType(final String identifier) {
		identifierString = identifier;
		this.identifier = new EventB(identifier);
	}

	public void addTypeArguments(final List<Type> arguments) {
		put(Type.class, arguments);
		typeArguments.addAll(arguments);
	}

	public void addConstructors(final List<DataTypeConstructor> constructors) {
		put(DataTypeConstructor.class, constructors);
		dataTypeConstructors.addAll(constructors);
	}

	public EventB getTypeIdentifier() {
		return identifier;
	}

	public List<DataTypeConstructor> getDataTypeConstructors() {
		return dataTypeConstructors;
	}

	public List<Type> getTypeArguments() {
		return typeArguments;
	}

	@Override
	public String toString() {
		return identifierString;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof DataType) {
			return identifierString.equals(((DataTypeConstructor) obj)
					.toString());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return identifierString.hashCode();
	}

	public void parseElements(final Set<IFormulaExtension> typeEnv) {
		for (DataTypeConstructor cons : dataTypeConstructors) {
			cons.parseElements(typeEnv);
		}
	}

	public Set<IFormulaExtension> getFormulaExtensions(final FormulaFactory ff) {
		if (typeDef == null) {
			typeDef = new DataTypeExtension(dataTypeConstructors);
		}
		return ff.makeDatatype(typeDef).getExtensions();
	}

	private class DataTypeExtension implements IDatatypeExtension {

		private final String unicodeDef;
		private final List<DataTypeConstructor> constructors;

		public DataTypeExtension(final List<DataTypeConstructor> constructors) {
			this.constructors = constructors;
			unicodeDef = identifierString;
		}

		@Override
		public String getTypeName() {
			return unicodeDef;
		}

		@Override
		public String getId() {
			return unicodeDef + " Datatype";
		}

		@Override
		public void addTypeParameters(final ITypeConstructorMediator mediator) {
			for (Type type : getTypeArguments()) {
				mediator.addTypeParam(UnicodeTranslator.toUnicode(type
						.getIdentifier().getCode()));
			}

		}

		@Override
		public void addConstructors(final IConstructorMediator mediator) {
			FormulaFactory factory = mediator.getFactory();

			Set<IFormulaExtension> exts = Collections
					.singleton((IFormulaExtension) mediator
							.getTypeConstructor());
			factory = factory.withExtensions(exts);
			for (DataTypeConstructor constructor : constructors) {
				List<DataTypeDestructor> destructors = constructor
						.getDestructors();
				String unicodeConstructorIdentifier = constructor.getUnicode();
				if (destructors.size() == 0) {
					mediator.addConstructor(unicodeConstructorIdentifier,
							unicodeConstructorIdentifier + " Constructor");
				} else {
					List<IArgument> arguments = new ArrayList<IArgument>();
					for (DataTypeDestructor dest : destructors) {
						String unicodeDestType = dest.getUnicodeType();
						String unicodeDestId = dest.getUnicodeIdentifier();

						org.eventb.core.ast.Type argumentType = factory
								.parseType(unicodeDestType, LanguageVersion.V2)
								.getParsedType();

						arguments.add(mediator.newArgument(unicodeDestId,
								mediator.newArgumentType(argumentType)));
					}
					mediator.addConstructor(unicodeConstructorIdentifier,
							unicodeConstructorIdentifier + " Constructor",
							arguments);
				}
			}
		}

		public List<DataTypeConstructor> getConstructors() {
			return constructors;
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) {
				return true;
			}
			if (o instanceof DataTypeExtension) {
				DataTypeExtension other = (DataTypeExtension) o;
				return constructors.equals(other.getConstructors())
						&& getId().equals(other.getId());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return getId().hashCode() * 13 + 23 * constructors.hashCode();
		}
	}
}