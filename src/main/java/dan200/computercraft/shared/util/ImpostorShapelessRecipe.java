/*
 * This file is part of ComputerCraft - http://www.computercraft.info
 * Copyright Daniel Ratcliffe, 2011-2017. Do not distribute without permission.
 * Send enquiries to dratcliffe@gmail.com
 */

package dan200.computercraft.shared.util;

import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IRecipeFactory;
import net.minecraftforge.common.crafting.JsonContext;

import javax.annotation.Nonnull;

public class ImpostorShapelessRecipe extends ShapelessRecipe
{
    public ImpostorShapelessRecipe( @Nonnull String group, @Nonnull ItemStack result, NonNullList<Ingredient> ingredients )
    {
        super( group, result, ingredients );
    }

    public ImpostorShapelessRecipe( @Nonnull String group, @Nonnull ItemStack result, ItemStack[] ingredients )
    {
        super( group, result, convert( ingredients ) );
    }

    private static NonNullList<Ingredient> convert( ItemStack[] items )
    {
        NonNullList<Ingredient> ingredients = NonNullList.withSize( items.length, Ingredient.EMPTY );
        for( int i = 0; i < items.length; i++ ) ingredients.set( i, Ingredient.fromStacks( items[ i ] ) );
        return ingredients;
    }

    @Override
    public boolean matches( CraftingInventory inv, World world )
    {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack getCraftingResult( CraftingInventory inventory )
    {
        return ItemStack.EMPTY;
    }

    public static class Factory implements IRecipeFactory
    {
        @Override
        public IRecipe parse( JsonContext context, JsonObject json )
        {
            String group = JSONUtils.getString( json, "group", "" );
            NonNullList<Ingredient> ings = RecipeUtil.getIngredients( context, json );
            ItemStack itemstack = ShapedRecipe.deserializeItem( JSONUtils.getJsonObject( json, "result" ), true );
            return new ImpostorShapelessRecipe( group, itemstack, ings );
        }
    }
}
