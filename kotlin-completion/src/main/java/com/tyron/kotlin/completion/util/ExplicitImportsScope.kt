package com.tyron.kotlin.completion.util

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.incremental.components.LookupLocation
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.resolve.scopes.BaseImportingScope
import org.jetbrains.kotlin.resolve.scopes.DescriptorKindFilter
import org.jetbrains.kotlin.utils.Printer
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

class ExplicitImportsScope(private val descriptors: Collection<DeclarationDescriptor>) : BaseImportingScope(null) {
    override fun getContributedClassifier(name: Name, location: LookupLocation) =
        descriptors.filter { it.name == name }.firstIsInstanceOrNull<ClassifierDescriptor>()

    override fun getContributedPackage(name: Name) = descriptors.filter { it.name == name }.firstIsInstanceOrNull<PackageViewDescriptor>()

    override fun getContributedVariables(name: Name, location: LookupLocation) =
        descriptors.filter { it.name == name }.filterIsInstance<VariableDescriptor>()

    override fun getContributedFunctions(name: Name, location: LookupLocation) =
        descriptors.filter { it.name == name }.filterIsInstance<FunctionDescriptor>()

    override fun getContributedDescriptors(
        kindFilter: DescriptorKindFilter,
        nameFilter: (Name) -> Boolean,
        changeNamesForAliased: Boolean
    ) = descriptors

    override fun computeImportedNames() = descriptors.mapTo(hashSetOf()) { it.name }

    override fun printStructure(p: Printer) {
        p.println(this::class.java.name)
    }
}