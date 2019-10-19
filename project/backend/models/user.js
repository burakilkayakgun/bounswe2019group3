'use strict';
module.exports = (sequelize, DataTypes) => {
  const User = sequelize.define('User', {
    username: {
      primaryKey: true,
      type: DataTypes.STRING,
      allowNull: false
    },
    email: {
      type: DataTypes.STRING,
      allowNull: false
    },
    bio: {
      type: DataTypes.STRING,
      allowNull: false
    },
    avatar: {
      type: DataTypes.STRING,
      allowNull: false
    },
    rating: {
      type: DataTypes.FLOAT,
      allowNull: false
    }
  });
  User.associate = function(models) {
    models.User.hasMany(models.Comment, {
        foreignKey: 'comment_to',
        as: 'comments',
        constraints: false
    });
    models.User.hasMany(models.Level, {
      foreignKey: 'belongs_to',
      as: 'grade',
      constraints: false
    });
  };
  return User;
};